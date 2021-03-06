/*
 * Copyright 2004-2012 ICEsoft Technologies Canada Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS
 * IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package com.icesoft.faces.context;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Date;

public class FileResource implements Resource, Serializable {
    private File file;

    public FileResource(File file) {
        this.file = file;
    }

    public String calculateDigest() {
        return file.getPath();
    }

    public Date lastModified() {
        return new Date(file.lastModified());
    }

    public InputStream open() throws IOException {
        return new FileInputStream(file);
    }

    public void withOptions(Options options) throws IOException {
        options.setLastModified(new Date(file.lastModified()));
        options.setFileName(file.getName());
    }
    
    public File getFile(){
    	return file;
    }
}
