/*
* Copyright 2004-2011 ICEsoft Technologies Canada Corp. (c)
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions an
* limitations under the License.
*/

#import "NativeInterface.h"
#import "QRScanner.h"
#import "AudioController.h"
#import "ARViewController.h"
#import "PlaceLabel.h"
#import "MobileCoreServices/MobileCoreServices.h"
#import "MediaPlayer/MediaPlayer.h"
#import "Logging.h"

#ifdef USE_ARMARKER
#import "ARMarkerViewer.h"
#endif

@implementation NativeInterface

@synthesize controller;
@synthesize userAgent;
@synthesize activeDOMElementId;
@synthesize maxwidth;
@synthesize maxheight;
@synthesize soundFilePath;
@synthesize nextFileIndex;
@synthesize recording;
@synthesize uploading;
@synthesize receivedData;
@synthesize qrScanner;
@synthesize currentPicker;
@synthesize camPopover;
@synthesize scanPopover;
@synthesize audioPopover;
@synthesize augPopover;
@synthesize augController;
@synthesize soundRecorder;
@synthesize popoverSource;
@synthesize motionManager;

static char base64EncodingTable[64] = {
  'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',
  'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f',
  'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
  'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'
};

- (id)init  {
    self = [super init];
    if (self) {
        self.qrScanner = [[QRScanner alloc] init];
        self.qrScanner.nativeInterface = self;
        NSString *tempDir = NSTemporaryDirectory ();
        self.soundFilePath = [tempDir stringByAppendingString: @"sound.mp4"];
        self.popoverSource = CGRectMake(200.0, 200.0, 0.0, 0.0);
    }
    
    return self;
}

- (void) dealloc  {
    [self.qrScanner dealloc];
    [super dealloc];
}

/*Return YES to indicate that the command was successfully dispatched
*/
- (BOOL)dispatch: (NSString*)command  {
    NSLog(@"NativeInterface dispatch %@ ", command);
    NSArray *queryParts = [command componentsSeparatedByString:@"?"];
    NSString *commandName = [queryParts objectAtIndex:0];
    NSDictionary *params;
    if ([queryParts count] > 1) {
        params = [self parseQuery:[queryParts objectAtIndex:1]];
    } else {
        params = [[[NSDictionary alloc] init] autorelease];
    }

    if ([@"register" isEqualToString:commandName])  {
        [self register];
    } else if ([@"camera" isEqualToString:commandName])  {
        [self camera:[params objectForKey:@"id"] 
                  maxwidth:[params objectForKey:@"maxwidth"]
                  maxheight:[params objectForKey:@"maxheight"] ];
    } else if ([@"camcorder" isEqualToString:commandName])  {
        [self camcorder:[params objectForKey:@"id"]];
    } else if ([@"upload" isEqualToString:commandName])  {
        [self upload:[params objectForKey:@"id"]];
    } else if ([@"microphone" isEqualToString:commandName])  {
        [self microphone:[params objectForKey:@"id"]];
    } else if ([@"play" isEqualToString:commandName])  {
        [self play:[params objectForKey:@"id"]];
    } else if ([@"scan" isEqualToString:commandName])  {
        [self scan:[params objectForKey:@"id"]];
    } else if ([@"aug" isEqualToString:commandName])  {
        [self aug:[params objectForKey:@"id"] locations:params];
    } else if ([@"fetchContacts" isEqualToString:commandName])  {
        [self address:[params objectForKey:@"id"]];
    }

    return YES;
}

- (BOOL)register  {
    [controller register];
    return YES;
}

- (BOOL)camera: (NSString*)cameraId maxwidth: (NSString*)maxw 
        maxheight: (NSString*)maxh {
    LogDebug(@"called camera");
    self.activeDOMElementId = cameraId;
    self.maxwidth = maxw;
    self.maxheight = maxh;
    UIImagePickerController* picker = [[UIImagePickerController alloc] init];
    picker.delegate = self;
    picker.allowsEditing = YES;
    if ([UIImagePickerController isSourceTypeAvailable:UIImagePickerControllerSourceTypeCamera ])  {
        picker.sourceType = UIImagePickerControllerSourceTypeCamera;
        picker.showsCameraControls = YES;
        CGRect overlayFrame = CGRectMake(0,picker.view.frame.size.height - 100,
            picker.view.frame.size.width, 100);
        UIView *overlayView = [[UIView alloc] initWithFrame:overlayFrame];
        UIButton *albumButton = [UIButton buttonWithType:UIButtonTypeRoundedRect];
        [albumButton setTitle:@"Album" forState:UIControlStateNormal];
        albumButton.alpha = 0.5;
        [albumButton setTitleColor:[UIColor blackColor] forState:UIControlStateNormal];
        albumButton.frame = CGRectMake(picker.view.frame.size.width - 70,
            10,60,30);
        [albumButton addTarget:self action:@selector(doAlbumButton:)
                forControlEvents:UIControlEventTouchDown];
        [overlayView addSubview:albumButton];
        picker.cameraOverlayView = overlayView;
        self.currentPicker = picker;
    }
    [self showImagePicker:picker];
    
    return YES;
}

- (void)doAlbumButton:(id)sender  {
    if (nil != self.currentPicker)  {
        self.currentPicker.sourceType = UIImagePickerControllerSourceTypePhotoLibrary;
    }
}

- (BOOL)play: (NSString*)audioId  {
    [controller play: audioId];
    return YES;
}

- (BOOL)microphone: (NSString*)micId  {
    self.activeDOMElementId = micId;
    UIView *controllerView = self.controller.view;


    AudioController *audioController = [[AudioController alloc] init];
    [[NSBundle mainBundle] loadNibNamed:@"AudioController" 
            owner:audioController options:nil];
    audioController.nativeInterface = self;
    if (UI_USER_INTERFACE_IDIOM() == UIUserInterfaceIdiomPad)  {
        if (nil == self.audioPopover)  {
            self.audioPopover = [[UIPopoverController alloc] 
                    initWithContentViewController:audioController];
            self.audioPopover.popoverContentSize = CGSizeMake(320, 480);
        }
        [self.audioPopover presentPopoverFromRect:popoverSource 
                                 inView:controllerView
               permittedArrowDirections:UIPopoverArrowDirectionAny 
                               animated:YES];
    } else {
        [controller presentModalViewController:audioController animated:YES];
    }
    [audioController release];
    
    return YES;
}

- (void)recordStart  {
    LogDebug(@"called recordStart");

    NSURL *soundFileURL = [[NSURL alloc] 
            initFileURLWithPath: self.soundFilePath];

 
    AVAudioSession *audioSession = [AVAudioSession sharedInstance];
//    audioSession.delegate = self;
    [audioSession setActive: YES error: nil];

    [[AVAudioSession sharedInstance]
            setCategory: AVAudioSessionCategoryRecord error: nil];
 
    NSDictionary *recordSettings =
        [[NSDictionary alloc] initWithObjectsAndKeys:
            [NSNumber numberWithFloat: 44100.0], AVSampleRateKey,
            [NSNumber numberWithInt: kAudioFormatMPEG4AAC], AVFormatIDKey,
//             [NSNumber numberWithInt: kAudioFormatLinearPCM], AVFormatIDKey,
//             [NSNumber numberWithInt: kAudioFormatAppleIMA4], AVFormatIDKey,
//             [NSNumber numberWithInt: kAudioFormatAppleLossless], AVFormatIDKey,
            [NSNumber numberWithInt: 1], AVNumberOfChannelsKey,
            [NSNumber numberWithInt: AVAudioQualityMax], AVEncoderAudioQualityKey,
            nil];
 
    AVAudioRecorder *avRecorder =
        [[AVAudioRecorder alloc] initWithURL: soundFileURL
                                    settings: recordSettings
                                       error: nil];
    self.soundRecorder = avRecorder;
//    soundRecorder.delegate = self;
    [soundRecorder prepareToRecord];
    LogDebug(@"recording started");
    [soundRecorder record];
    self.recording = YES;

}

- (void)recordStop  {
    LogDebug(@"recording stopped");
    [self.soundRecorder stop];
}

- (void)recordDone  {
    [self recordDismiss];
    NSString *audioName = self.activeDOMElementId;
    [controller completeFile:self.soundFilePath 
            forComponent:audioName withName:audioName];
}

- (void)recordDismiss  {
    if (nil != self.audioPopover)  {
        [self.audioPopover dismissPopoverAnimated:YES];
    } else {
        [controller dismissModalViewControllerAnimated:YES];
    }
}

- (BOOL)camcorder: (NSString*)cameraId  {
    self.activeDOMElementId = cameraId;
    UIImagePickerController* picker = [[UIImagePickerController alloc] init];
    picker.delegate = self;
    picker.allowsEditing = YES;
    picker.mediaTypes =  [[NSArray alloc] initWithObjects: (NSString *) kUTTypeMovie, nil];
    if ([UIImagePickerController isSourceTypeAvailable:UIImagePickerControllerSourceTypeCamera ])  {
        picker.sourceType = UIImagePickerControllerSourceTypeCamera;
    }
 
    [self showImagePicker:picker];
    
    return YES;
}

- (void)imagePickerController:(UIImagePickerController *)picker
                    didFinishPickingImage:(UIImage *)image
                    editingInfo:(NSDictionary *)editingInfo  {
    NSString *cameraId = self.activeDOMElementId;
    NSString *cameraName = cameraId;

    UIImage *uploadImage = image;
    if ((nil != self.maxwidth) && (nil != self.maxheight))  {
        int maxwI = [self.maxwidth intValue];
        int maxhI = [self.maxheight intValue];
        self.maxwidth = nil;
        self.maxheight = nil;
        uploadImage = [self scaleImage:image maxWidth:maxwI maxHeight:maxhI];
    }

    NSString *savedPath = [self saveImage:uploadImage];
    LogDebug(@"called camera and saved %@ for %@", savedPath, cameraId);
    // Remove the picker interface and release the picker object.
    [self dismissImagePicker];
    [picker release];

    UIImage *scaledImage = [self scaleImage:image toSize:64];
    [self setThumbnail:scaledImage at:cameraId];

    [controller completeFile:savedPath 
            forComponent:cameraId withName:cameraName];
}

- (void) imagePickerController: (UIImagePickerController *)picker
            didFinishPickingMediaWithInfo: (NSDictionary *)info  {
    LogDebug(@"imagePickerController didFinishPickingMediaWithInfo %@", info);
    UIImage *editedImage = (UIImage *) [info objectForKey:
                    UIImagePickerControllerEditedImage];
    if (nil != editedImage)  {
        [self imagePickerController:picker didFinishPickingImage:editedImage editingInfo:info];
        return;
    }
    NSString *cameraId = self.activeDOMElementId;
    NSString *cameraName = cameraId;
    NSURL *movieURL = [info objectForKey: UIImagePickerControllerMediaURL];
    NSString *moviePath = [movieURL path];

    MPMoviePlayerController *movieController = 
            [[MPMoviePlayerController alloc] initWithContentURL:movieURL];
    movieController.shouldAutoplay = NO;
    movieController.initialPlaybackTime = 0;
    movieController.currentPlaybackTime = 0;
    UIImage *image = [movieController thumbnailImageAtTime:0 
                           timeOption:MPMovieTimeOptionNearestKeyFrame];

    UIImage *scaledImage = [self scaleImage:image toSize:64];
    [self setThumbnail:scaledImage at:cameraId];

    [controller completeFile:moviePath
            forComponent:cameraId withName:cameraName];

    [self dismissImagePicker];
    [picker release];
}

- (void)showImagePicker: (UIImagePickerController*)picker {
    if (UI_USER_INTERFACE_IDIOM() == UIUserInterfaceIdiomPad)  {
        self.camPopover = [[UIPopoverController alloc] initWithContentViewController:picker];
        //[picker release];
        [self.camPopover presentPopoverFromRect:popoverSource
                inView:self.controller.view
                permittedArrowDirections:UIPopoverArrowDirectionAny 
                animated:YES];
    } else {
        [controller presentModalViewController:picker animated:YES];
    }
}

- (void)dismissImagePicker {
    if (nil != self.camPopover)  {
        [self.camPopover dismissPopoverAnimated:YES];
    } else {
        [self.controller dismissModalViewControllerAnimated:YES];
    }
    self.currentPicker = nil;
}

- (void)setThumbnail: (UIImage*)image at: (NSString *)thumbId  {
    [controller setThumbnail:image at:thumbId];
}

- (void)imagePickerControllerDidCancel:(UIImagePickerController *)picker {
    LogTrace(@"imagePickerControllerDidCancel");
    [self dismissImagePicker];
    [picker release];
    [self.controller doCancel];
}

- (UIImage*)scaleImage: (UIImage*)image maxWidth: (int)maxw maxHeight: (int)maxh  {
    CGSize imageSize = [image size];
    CGFloat maxwF = (CGFloat) maxw;
    CGFloat maxhF = (CGFloat) maxh;
    CGFloat factorX = 1.0;
    CGFloat factorY = 1.0;
    if (imageSize.width > maxw)  {
        factorX = maxwF / imageSize.width;
    }
    if (imageSize.height > maxh)  {
        factorY = maxhF / imageSize.height;
    }

    CGFloat factor = MIN(factorX, factorY);
    
    LogDebug(@"scaling image with factor %f %f", factorX, factorY);
    CGSize bufSize = CGSizeMake(maxwF, maxhF);
    UIGraphicsBeginImageContext(bufSize);
    [image drawInRect:CGRectMake(0, 0, imageSize.width * factor, 
            imageSize.height * factor)];
    UIImage* scaledImage = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    return scaledImage;

}

- (UIImage*)scaleImage: (UIImage*)image toSize: (int)finalSize  {
    return [self scaleImage:image maxWidth:finalSize maxHeight:finalSize];
}

- (NSString*)saveImage: (UIImage*)image  {
    NSString *imageName = [NSString stringWithFormat:@"image%d.jpg",nextFileIndex++];
    NSString *imagePath = [NSTemporaryDirectory() 
            stringByAppendingPathComponent:imageName];
    [UIImageJPEGRepresentation(image, 0.7) writeToFile:imagePath atomically:NO];
    return imagePath;
}


- (BOOL)scan: (NSString*)scanId  {
    self.activeDOMElementId = scanId;
    LogTrace(@"NativeInterface scan ");
    UIViewController *scanController = [qrScanner scanController];
    if (UI_USER_INTERFACE_IDIOM() == UIUserInterfaceIdiomPad)  {
        if (nil == self.scanPopover)  {
            scanPopover = [[UIPopoverController alloc] 
                    initWithContentViewController:scanController];
            self.scanPopover.popoverContentSize = CGSizeMake(320, 480);
        }
        [self.scanPopover presentPopoverFromRect:popoverSource
                                 inView:self.controller.view
               permittedArrowDirections:UIPopoverArrowDirectionAny 
                               animated:YES];
    } else {
        [controller presentModalViewController:scanController animated:YES];
    }
    [scanController release];

    return YES;
}

- (void)scanResult: (NSString*)text  {
    NSString *scanName = self.activeDOMElementId;
    [controller completePost:text forComponent:scanName
            withName:scanName];
}

- (void)dismissScan {
    if (nil != self.scanPopover)  {
        [self.scanPopover dismissPopoverAnimated:YES];
    } else {
        [controller dismissModalViewControllerAnimated:YES];
    }
}

- (BOOL)address: (NSString*)contactId  {
    NSLog(@"NativeInterface address ");

    self.activeDOMElementId = contactId;
    ABPeoplePickerNavigationController* picker = [[ABPeoplePickerNavigationController alloc] init];
    picker.peoplePickerDelegate = self;
    [self.controller presentModalViewController:picker animated:YES];

    return YES;
}

- (void)dismissAddress {
//    if (nil != self.addressPopover)  {
//        [self.addressPopover dismissPopoverAnimated:YES];
//    } else {
        [controller dismissModalViewControllerAnimated:YES];
//    }
}

- (BOOL)peoplePickerNavigationController:(ABPeoplePickerNavigationController *)peoplePicker shouldContinueAfterSelectingPerson:(ABRecordRef)person {
    NSLog(@"NativeInterface address selected person");


    NSString *firstName = ABRecordCopyValue(person, kABPersonFirstNameProperty);
    if (nil == firstName)  {
        firstName = @"";
    }
    NSString *lastName  = ABRecordCopyValue(person, kABPersonLastNameProperty);
    if (nil == lastName)  {
        lastName = @"";
    }

    ABMutableMultiValueRef multi;

    multi = ABRecordCopyValue(person, kABPersonEmailProperty);
    NSString *emailResult = [self getOneFromMultiRef:multi];
    CFRelease(multi);

    multi = ABRecordCopyValue(person, kABPersonPhoneProperty);
    NSString *phone = [self getOneFromMultiRef:multi];
    CFRelease(multi);

    NSString *fullName = [NSString stringWithFormat:@"%@ %@", firstName, lastName];

    NSString *result = [NSString stringWithFormat:@"name=%@&email=%@&phone=%@", 
            [fullName stringByAddingPercentEscapesUsingEncoding:NSASCIIStringEncoding], 
            [emailResult stringByAddingPercentEscapesUsingEncoding:NSASCIIStringEncoding], 
            [phone stringByAddingPercentEscapesUsingEncoding:NSASCIIStringEncoding] ];

NSLog(@"Found record %@", result);

    [self dismissAddress];
    NSString *addressName = self.activeDOMElementId;
    [controller completePost:result forComponent:addressName
            withName:addressName];

    return NO;
}

- (NSString*)getOneFromMultiRef:(ABMutableMultiValueRef) multi  {
    NSString *result = @"";

    if (ABMultiValueGetCount(multi) > 0) {
        for (CFIndex i = 0; i < ABMultiValueGetCount(multi); i++) {
            CFTypeRef cfValue = ABMultiValueCopyValueAtIndex(multi, i);
            CFTypeID cfType = CFGetTypeID(cfValue);
            if (cfType == CFStringGetTypeID()) {
                result = (NSString *)cfValue;
            } else {
                result = @"notCFString";
            }
            CFRelease(cfValue);
        }
    }

    return result;
}

- (BOOL)peoplePickerNavigationController:(ABPeoplePickerNavigationController *)peoplePicker shouldContinueAfterSelectingPerson:(ABRecordRef)person property:(ABPropertyID)property identifier:(ABMultiValueIdentifier)identifier {
    return NO;
}

- (void)peoplePickerNavigationControllerDidCancel:(ABPeoplePickerNavigationController *)peoplePicker  {
    NSLog(@"NativeInterface peoplePickerNavigationControllerDidCancel ");
    [self dismissAddress];
}

- (BOOL)aug: (NSString*)augId locations:(NSDictionary *)places {
    self.activeDOMElementId = augId;
    LogTrace(@"NativeInterface aug ");
    if (nil == self.augController)  {
        if (UI_USER_INTERFACE_IDIOM() == UIUserInterfaceIdiomPad)  {
            self.augController = [[ARViewController alloc] 
                    initWithNibName:@"ARViewController_iPhone" bundle:nil];
        } else {
            augController = [[ARViewController alloc] 
                    initWithNibName:@"ARViewController_iPhone" bundle:nil];
        }
        augController.nativeInterface = self;
    }
    NSString *viewer = [places objectForKey:@"v"];
    LogDebug(@"NativeInterface aug VIEWER %@", viewer);
    if ([viewer isEqualToString:@"vuforia"])  {
        [self augMarkerView:augId withMarkers:places];
        return YES;
    }


    NSString *urlBase = [places objectForKey:@"ub"];
	NSMutableArray *placeLabels = [NSMutableArray array];
    for (NSString *name in places)  {
        if ([name isEqualToString:@"id"])  {
            continue;
        }
        if ([name isEqualToString:@"ub"])  {
            continue;
        }
        if ([name isEqualToString:@"v"])  {
            continue;
        }
        NSArray *pairs = [[places objectForKey:name] 
                componentsSeparatedByString:@","];
        double lat = 0;
        double lon = 0;
        double alt = 0;
        double hed = -1;
        NSString *iconName = nil;
        NSURL *url;
        if ([pairs count] > 1)  {
            lat = [[pairs objectAtIndex:0] doubleValue];
            lon = [[pairs objectAtIndex:1] doubleValue];
        }
        if ([pairs count] > 2)  {
            alt = [[pairs objectAtIndex:2] doubleValue];
        }
        if ([pairs count] > 3)  {
            hed = [[pairs objectAtIndex:3] doubleValue];
        }
        if ([pairs count] > 4)  {
            iconName = [pairs objectAtIndex:4];
        }

        PlaceLabel *label = nil;
        if (nil == iconName)  { 
            label = [PlaceLabel 
                placeLabelWithText:name initWithLatitude:lat longitude:lon];
        } else {
            if (nil == urlBase)  {
                url = [NSURL URLWithString:iconName];
            } else {
                url = [NSURL URLWithString:
                    [urlBase stringByAppendingString:iconName]];
            }
            //this needs to run asynchronously
            NSData *data = [NSData dataWithContentsOfURL:url];
            UIImage *iconImage = [UIImage imageWithData:data];
            label = [PlaceLabel 
                placeLabelWithText:name andImage:iconImage
                initWithLatitude:lat longitude:lon];
        }
        if (hed >= 0)  {
            label.heading = hed;
        }
        [placeLabels addObject:label];
        LogDebug(@"loaded marker %@ coords %f,%f %f %@", name, lat,lon, hed, url);

    }
    [self.augController setPlaceLabels:placeLabels];


    if (UI_USER_INTERFACE_IDIOM() == UIUserInterfaceIdiomPad)  {
        if (nil == self.augPopover)  {
            augPopover = [[UIPopoverController alloc] 
                    initWithContentViewController:augController];
            self.augPopover.popoverContentSize = CGSizeMake(320, 480);
        }
        [self.augPopover presentPopoverFromRect:popoverSource
                                 inView:self.controller.view
               permittedArrowDirections:UIPopoverArrowDirectionAny 
                               animated:YES];
    } else {
        self.augController.oldView = controller.view;
        UIView *containerView = controller.view.superview;
        [UIView transitionWithView:containerView duration:0.5
            options:UIViewAnimationOptionTransitionFlipFromRight
            animations:^ { [controller.view removeFromSuperview];
            [containerView addSubview:self.augController.view]; }
            completion:nil];
    }

    return YES;
}

- (void)augMarkerView:(NSString*) augId withMarkers:(NSDictionary *)markers  {
#ifdef USE_ARMARKER
    LogTrace(@"NativeInterface augMarkerView");

    if (UI_USER_INTERFACE_IDIOM() == UIUserInterfaceIdiomPad)  {
//        if (nil == self.augPopover)  {
//            augPopover = [[UIPopoverController alloc]
//                    initWithContentViewController:augController];
//            self.augPopover.popoverContentSize = CGSizeMake(320, 480);
//        }
//        [self.augPopover presentPopoverFromRect:popoverSource
//                                 inView:self.controller.view
//               permittedArrowDirections:UIPopoverArrowDirectionAny 
//                               animated:YES];
    } else {
        ARMarkerViewer *arMarkerViewer = [[ARMarkerViewer alloc] init];
        arMarkerViewer.nativeInterface = self;
        arMarkerViewer.arViewRect = [[UIScreen mainScreen] bounds];
        arMarkerViewer.markers = markers;
        [controller presentModalViewController:[arMarkerViewer arMarkerController] animated:YES];
    }

#endif
}

- (void)augHide  {
    if (nil != self.augPopover)  {
        [self.augPopover dismissPopoverAnimated:YES];
    } else {
        UIView *containerView = augController.view.superview;
        [UIView transitionWithView:containerView duration:0.5
            options:UIViewAnimationOptionTransitionFlipFromLeft
            animations:^ { [augController.view removeFromSuperview];
            [containerView addSubview:augController.oldView]; }
            completion:nil];
    }
}

- (void)augMarkerHide  {
    [self.controller dismissModalViewControllerAnimated:YES];
}

- (void)augMarkerDismiss  {
    [self augMarkerHide];
    [self.controller doCancel];
}

- (void)augDismiss  {
    [self.augController stop];
    [self augHide];
    [self.controller doCancel];
}

- (void)augDone  {
    [self augHide];
    NSString *augName = self.activeDOMElementId;
    NSString *augResult = self.augController.selectedPlace;
    LogDebug(@"NativeInterface aug selected %@", augResult);
    [controller completePost:augResult forComponent:augName
            withName:augName];
}

- (void)startMotionManager {
    if (nil != self.motionManager)  {
        return;
    }

    self.motionManager = [[CMMotionManager alloc] init];

    if (![self.motionManager respondsToSelector:
            @selector(setShowsDeviceMovementDisplay:)])  {
        LogError(@"NativeInterface deviceMotion not supported");
        return;
    }

	// Tell CoreMotion to show the compass calibration HUD when required to provide true north-referenced attitude
	self.motionManager.showsDeviceMovementDisplay = YES;
	
	self.motionManager.deviceMotionUpdateInterval = 1.0 / 60.0;
	
	// New in iOS 5.0: Attitude that is referenced to true north
//True North causes lingering core motion thread that crashes upon app return
//	[motionManager startDeviceMotionUpdatesUsingReferenceFrame:CMAttitudeReferenceFrameXTrueNorthZVertical];
	[self.motionManager startDeviceMotionUpdatesUsingReferenceFrame:CMAttitudeReferenceFrameXMagneticNorthZVertical];
}

- (void)stopMotionManager  {
    if (nil == self.motionManager)  {
        return;
    }
    [self.motionManager stopDeviceMotionUpdates];
    [self.motionManager release];
    self.motionManager = nil;
}

- (BOOL)upload: (NSString*)formId  {
    if (self.uploading)  {
    LogInfo(@" already uploading, ignoring request");
        return NO;
    }
    self.uploading = YES;
    self.activeDOMElementId = formId;

    [controller setProgress:0];
    NSString* actionURL = [controller prepareUpload:formId];
    NSDictionary *parts = [self parseQuery:[controller getFormData:formId]];
    [self multipartPost:parts toURL:actionURL];

    return YES;
}

- (NSString *) guessMimeType:(NSString *)path {
    if ([path hasSuffix:@".jpg"])  {
        return @"Content-Type: image/jpeg\r\n\r\n";
    }
    if ([path hasSuffix:@".m4a"])  {
        return @"Content-Type: audio/m4a\r\n\r\n";
    }
    if ([path hasSuffix:@".mp4"])  {
        return @"Content-Type: audio/mp4\r\n\r\n";
    }
    if ([path hasSuffix:@".caf"])  {
        return @"Content-Type: audio/x-caf\r\n\r\n";
    }
    if ([path hasSuffix:@".wav"])  {
        return @"Content-Type: audio/x-wav\r\n\r\n";
    }
    if ([path hasSuffix:@".MOV"])  {
        return @"Content-Type: video/mp4\r\n\r\n";
    }
    return @"text/plain";
}

/*Return NSDictionary of query parameters
*/
- (NSMutableDictionary*)parseQuery: (NSString*)queryString  {
    if ((nil == queryString) || (0 == [queryString length]))  {
        return [NSMutableDictionary dictionary];
    }
    NSArray *pairs = [queryString componentsSeparatedByString:@"&"];
    NSMutableDictionary *pairDict = [NSMutableDictionary dictionary];
    for (NSString *pair in pairs) {
        NSArray *pairPair = [pair componentsSeparatedByString:@"="];
        if ([pairPair count] < 2)  {
            continue;
        }
        NSString *name = [[pairPair objectAtIndex:0] 
                stringByReplacingPercentEscapesUsingEncoding:NSASCIIStringEncoding];
        NSString *value = [[pairPair objectAtIndex:1] 
            stringByReplacingPercentEscapesUsingEncoding:NSASCIIStringEncoding];
        [pairDict setObject:value forKey:name];
    }

    return pairDict;
}

- (NSString *) base64StringFromData: (NSData *)data {
    int lentext = [data length]; 
    if (lentext < 1) return @"";

    char *outbuf = malloc(lentext*4/3+4); // add 4 to be sure

    if ( !outbuf ) return nil;

    const unsigned char *raw = [data bytes];

    int inp = 0;
    int outp = 0;
    int do_now = lentext - (lentext%3);

    for ( outp = 0, inp = 0; inp < do_now; inp += 3 )
    {
        outbuf[outp++] = base64EncodingTable[(raw[inp] & 0xFC) >> 2];
        outbuf[outp++] = base64EncodingTable[((raw[inp] & 0x03) << 4) | ((raw[inp+1] & 0xF0) >> 4)];
        outbuf[outp++] = base64EncodingTable[((raw[inp+1] & 0x0F) << 2) | ((raw[inp+2] & 0xC0) >> 6)];
        outbuf[outp++] = base64EncodingTable[raw[inp+2] & 0x3F];
    }

    if ( do_now < lentext )
    {
        unsigned char tmpbuf[2] = {0,0};
        int left = lentext%3;
        for ( int i=0; i < left; i++ )
        {
            tmpbuf[i] = raw[do_now+i];
        }
        raw = tmpbuf;
        outbuf[outp++] = base64EncodingTable[(raw[inp] & 0xFC) >> 2];
        outbuf[outp++] = base64EncodingTable[((raw[inp] & 0x03) << 4) | ((raw[inp+1] & 0xF0) >> 4)];
        if ( left == 2 ) outbuf[outp++] = base64EncodingTable[((raw[inp+1] & 0x0F) << 2) | ((raw[inp+2] & 0xC0) >> 6)];
    }

    int padLen = ((lentext + 2) - ((lentext + 2) % 3)) / 3 * 4;
    int needPad = padLen - outp;
    if (1 == needPad)  {
        outbuf[outp++] = '=';
    } else if (2 == needPad)  {
        outbuf[outp++] = '=';
        outbuf[outp++] = '=';
    }

    NSString *ret = [[[NSString alloc] initWithBytes:outbuf length:outp encoding:NSASCIIStringEncoding] autorelease];
    free(outbuf);

    return ret;
}

- (void)multipartPost: (NSDictionary *)parts toURL: (NSString *)url  {
    NSString *filename = @"changethisvalue.tmp";
    NSMutableURLRequest *request = [[[NSMutableURLRequest alloc] init] autorelease];
    [request setURL:[NSURL URLWithString:url]];
    [request setHTTPMethod:@"POST"];
    if (nil != self.userAgent)  {
        [request addValue:self.userAgent forHTTPHeaderField: @"User-Agent"];
    }
    CFUUIDRef uuid = CFUUIDCreate(NULL);
    CFStringRef uuidCFString = CFUUIDCreateString(NULL, uuid);
    NSString *uuidString = [(NSString *) uuidCFString autorelease];
    CFRelease(uuid);
    NSString *boundary = [NSString stringWithFormat:@"----%@----",uuidString];
    NSString *contentType = [NSString stringWithFormat:@"multipart/form-data; boundary=%@",boundary];
    [request addValue:contentType forHTTPHeaderField: @"Content-Type"];
    [request addValue:@"partial/ajax" forHTTPHeaderField:@"Faces-Request"];
    NSMutableData *postbody = [NSMutableData data];
    for (NSString *key in parts)  {
        LogDebug(@"multipartPost part %@ equals %@", key, [parts objectForKey:key] );
        NSString *value = [parts objectForKey:key];
        NSString *mimeTypeHeader = [self guessMimeType:value];
        int nameSplit = [key rangeOfString:@"-"].location;
        NSString *partName = [key substringFromIndex:nameSplit + 1];
        NSString *partType = [key substringToIndex:nameSplit];
        if ([partType isEqualToString:@"file"] ) {
            NSLog(@"    writing file part for %@ %@", partName, filename );
            [postbody appendData:[[NSString stringWithFormat:@"\r\n--%@\r\n",boundary] dataUsingEncoding:NSUTF8StringEncoding]];
            [postbody appendData:[[NSString stringWithFormat:@"Content-Disposition: form-data; name=\"%@\"; filename=\"%@\"\r\n", partName, filename] dataUsingEncoding:NSUTF8StringEncoding]];
            [postbody appendData:[[NSString stringWithString:mimeTypeHeader] dataUsingEncoding:NSUTF8StringEncoding]];
            [postbody appendData:[NSData dataWithContentsOfFile:value]];
        } else  {
            //normal form field
            //Content-Disposition: form-data; name="text1"
            LogDebug(@"    writing normal part for %@", partName );
            [postbody appendData:[[NSString stringWithFormat:@"\r\n--%@\r\n",boundary] dataUsingEncoding:NSUTF8StringEncoding]];
            [postbody appendData:[[NSString stringWithFormat:@"Content-Disposition: form-data; name=\"%@\"\r\n\r\n", partName] dataUsingEncoding:NSUTF8StringEncoding]];
            [postbody appendData:[value dataUsingEncoding:NSUTF8StringEncoding]];
        }
    }
    [postbody appendData:[[NSString stringWithFormat:@"\r\n--%@--\r\n",boundary] dataUsingEncoding:NSUTF8StringEncoding]];

    [request setHTTPBody:postbody];

    NSURLConnection *theConnection=[[NSURLConnection alloc] initWithRequest:request delegate:self];
    if (theConnection) {
        receivedData = [[NSMutableData data] retain];
    } else {
        NSLog(@"unable to connect to %@", theConnection);
    }

    return;
}

- (void)connection:(NSURLConnection *)connection didFailWithError:(NSError *)error  {
    self.uploading = NO;
    NSLog(@"didFailWithError %@", error);
    UIAlertView *alert = [[UIAlertView alloc] 
            initWithTitle:@"Connection Failure" 
            message:error.localizedDescription
            delegate:nil cancelButtonTitle:@"OK" 
            otherButtonTitles:nil];

    [alert show];
    [alert release];
}

- (void)connection:(NSURLConnection *)connection didReceiveResponse:(NSURLResponse *)response  {
    [receivedData setLength:0];
}

- (void)connection:(NSURLConnection *)connection didReceiveData:(NSData *)data  {
    [receivedData appendData:data];
}

- (void)connection:(NSURLConnection *)connection didSendBodyData:(NSInteger)bytesWritten totalBytesWritten:(NSInteger)totalBytesWritten totalBytesExpectedToWrite:(NSInteger)totalBytesExpectedToWrite  {
    LogDebug(@"didSendBodyData %d bytes of %d",totalBytesWritten, totalBytesExpectedToWrite);
    NSInteger percentProgress = (totalBytesWritten * 100) / totalBytesExpectedToWrite;
    [controller setProgress:percentProgress];
}

- (void)connectionDidFinishLoading:(NSURLConnection *)connection  {
    LogDebug(@"connectionDidFinishLoading %d bytes of data",[receivedData length]);

    NSString *responseString = [[NSString alloc] initWithData:receivedData
            encoding:NSUTF8StringEncoding];

    [controller setProgress:100];
    [controller handleResponse:responseString];
    [responseString release];

    // release the connection, and the data object
    [connection release];
    [receivedData release];
    self.uploading = NO;
}

- (void)applicationWillResignActive {

    if (nil != self.augController)  {
        [self augDismiss];
    }
    [self stopMotionManager];

    NSLog(@"NativeInterface applicationWillResignActive");
}

@end
