insert into Bidder (username, password, name, role) values ('jguglielmin', 'jjjjj', 'Judy Guglielmin', 'admin')
insert into Bidder (username, password, name, role) values ('demo', 'demo', 'Demo User', 'user')
insert into Bidder (username, password, name, role) values ('brad', 'demo', 'Brad Kroeger', 'user')
insert into Bidder (username, password, name, role) values ('yip', 'demo', 'Yip Ng', 'user')
insert into Bidder (username, password, name, role) values ('patrick', 'demo', 'Patrick Corless', 'admin')
insert into auctionitem (itemId, bidCount,currency,description,imageFile,location,price,seller,site,title,expires) values (4501921328,0,'USD','Used icebreaker with very few dents, comes with manual.','img/icebreaker.jpg','Calgary, Alberta Canada',5.0,'ICEsoft Technologies, Inc.','http://www.icesoft.com','ICEsoft Icebreaker','2008-02-15 00:00:00')
insert into auctionitem (itemId, bidCount,currency,description,imageFile,location,price,seller,site,title,expires) values (4501908972,0,'USD','A single sharpened ice skate, size 7.','img/iceskate.jpg','Calgary, Alberta Canada',100.0,'ICEsoft Technologies, Inc.','http://www.icesoft.com','ICEsoft Ice Skate','2008-02-23 00:00:00')
insert into auctionitem (itemId, bidCount,currency,description,imageFile,location,price,seller,site,title,expires) values (4501921327,0,'USD','Beautiful ice car with metal car filling.','img/icecar.jpg','Calgary, Alberta Canada',10.0,'ICEsoft Technologies, Inc.','http://www.icesoft.com','ICEsoft Ice Car','2008-3-01 00:00:00')
insert into auctionitem (itemId, bidCount,currency,description,imageFile,location,price,seller,site,title,expires) values (4501921417,0,'USD','Put him on the ice and watch him go!  Requires food and water.', 'img/icesailor.jpg','Calgary, Alberta Canada',10000.0,'ICEsoft Technologies, Inc.','http://www.icesoft.com','ICEsoft Ice Sailor','2007-12-30 00:00:00')
insert into Bid (user_username, auctionItem_itemId, bidValue, timestamp, creditCard, creditCardName, creditCardExpiryMonth, creditCardExpiryYear) values ('jguglielmin', 4501921327, 12.34, '2007-06-08 15:13:03', '1234567890123456', 'Visa', 10, 2008)
insert into Bid (user_username, auctionItem_itemId, bidValue, timestamp, creditCard, creditCardName, creditCardExpiryMonth, creditCardExpiryYear) values ('demo', 4501921328, 88.88, '2007-04-29 19:38:29', '5896425631025489', 'Master Card', 4, 2010)
insert into Bid (user_username, auctionItem_itemId, bidValue, timestamp, creditCard, creditCardName, creditCardExpiryMonth, creditCardExpiryYear) values ('patrick', 4501921328, 107, '2007-04-30 19:38:29', '5896425631025489', 'Master Card', 4, 2010)
insert into Bid (user_username, auctionItem_itemId, bidValue, timestamp, creditCard, creditCardName, creditCardExpiryMonth, creditCardExpiryYear) values ('brad', 4501908972, 253.2, '1998-05-02 01:23:56.123', '6952145698745258', 'Amex', 6, 2009)
insert into Bid (user_username, auctionItem_itemId, bidValue, timestamp, creditCard, creditCardName, creditCardExpiryMonth, creditCardExpiryYear) values ('yip', 4501908972, 288, '2001-11-01 22:13:09.234', '3659487960215847', 'CapitalOne', 8, 2020)
insert into Bid (user_username, auctionItem_itemId, bidValue, timestamp, creditCard, creditCardName, creditCardExpiryMonth, creditCardExpiryYear) values ('demo', 4501908972, 568, '2002-10-30 11:27:38.567', '3659487960215847', 'CapitalOne', 8, 2020)
insert into Bid (user_username, auctionItem_itemId, bidValue, timestamp, creditCard, creditCardName, creditCardExpiryMonth, creditCardExpiryYear) values ('demo', 4501921417, 10000, '2001-11-15 20:13:09.234', '1234123412341234', 'Amex', 5, 2015)
