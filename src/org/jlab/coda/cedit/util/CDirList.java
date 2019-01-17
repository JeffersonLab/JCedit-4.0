/*
 *   Copyright (c) 2016.  Jefferson Lab (JLab). All rights reserved. Permission
 *   to use, copy, modify, and distribute  this software and its documentation for
 *   educational, research, and not-for-profit purposes, without fee and without a
 *   signed licensing agreement.
 *
 *   IN NO EVENT SHALL JLAB BE LIABLE TO ANY PARTY FOR DIRECT, INDIRECT, SPECIAL
 *   INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING LOST PROFITS, ARISING
 *   OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF JLAB HAS
 *   BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *   JLAB SPECIFICALLY DISCLAIMS ANY WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 *   THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 *   PURPOSE. THE CLARA SOFTWARE AND ACCOMPANYING DOCUMENTATION, IF ANY,
 *   PROVIDED HEREUNDER IS PROVIDED "AS IS". JLAB HAS NO OBLIGATION TO PROVIDE
 *   MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS.
 *
 *   This software was developed under the United States Government license.
 *   For more information contact author at gurjyan@jlab.org
 *   Department of Experimental Nuclear Physics, Jefferson Lab.
 */

package org.jlab.coda.cedit.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

public class CDirList {
    private ArrayList<File> list = new ArrayList<>();

    public CDirList(String dirPath, final String fileNameFilter) {

        File dir = new File(dirPath);
        File[] files;
        if (fileNameFilter != null && !fileNameFilter.equals("") && !fileNameFilter.equals("*")) {
            //file name filter accepts * for filtering
            FilenameFilter fnf = (dir1, name) -> {
                StringTokenizer st = new StringTokenizer(fileNameFilter, "*");
                List<String> tokens = new ArrayList<>();
                while (st.hasMoreElements()) {
                    tokens.add(st.nextElement().toString().trim());
                }
                for (String t : tokens) {
                    if (!name.contains(t)) return false;
                    if( Collections.frequency(tokens, t) != count(name,t)) return false;
                }
                return true;
            };
            files = dir.listFiles(fnf);
        } else {
            files = dir.listFiles();
        }
        // get the expId directory
        if (files != null && files.length > 0) {
            for (final File file : files) {
                if (file.isDirectory() && !file.getName().equals(".svn")) {
                    list.add(file);
                }
            }
        }
    }


    public String[] getNames() {
        ArrayList<String> o = new ArrayList<>();
        for (File f : list) {
            o.add(f.getName());
        }
        Collections.sort(o);
        return o.toArray(new String[o.size()]);
    }

    public String getAbsolutePath(String name) {
        for (File f : list) {
            if (f.getName().equals(name)) {
                return f.getAbsolutePath();
            }
        }
        return "undefined";
    }

    private int count(String str, String target) {
        return (str.length() - str.replace(target, "").length()) / target.length();
    }
}
