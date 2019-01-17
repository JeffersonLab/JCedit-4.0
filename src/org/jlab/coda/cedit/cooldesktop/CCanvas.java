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

package org.jlab.coda.cedit.cooldesktop;

import org.jlab.coda.cedit.system.*;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class CCanvas extends CanvasDropTarget {
    private String gCmpName;
    private String gCmpImageFile;

    private int yPage       = 0;
    private int currentX    = -120;
    HashMap<String, Point> typePositions = new HashMap<String, Point>();

    public DrawingCanvas drawingCanvas;



    public CCanvas(DrawingCanvas cv){
        super(cv);
        drawingCanvas = cv;
    }




    public void dropAction(String o){
        String type;
        o = o.trim();
        JCGComponent gc = new JCGComponent();


        // predefined components drag and drop
        if(o.contains(":^")){
            int id = -1;
            gCmpName = o.substring(0,o.lastIndexOf(":^")).trim();
            type = o.substring(o.indexOf(":^")+2,o.indexOf("#^")).trim();
//            updateIdStacks(type);
            try{
                id = Integer.parseInt(o.substring(o.indexOf("@^")+2,o.indexOf("%^")).trim());
            } catch(NumberFormatException e){
                System.out.println(e.getMessage());
            }

            if(!CDesktop.defineRocMastership(gCmpName,type, gc)) return;

            gCmpImageFile = File.separator+"resources"+File.separator+type+".png";
            gc.setPreDefined(true);
            gc.setType(type);
            gc.setId(id);
            gc.setPriority(ACodaType.getEnum(type).priority());
            if(o.lastIndexOf("%^")>0){
                gc.setDescription(o.substring(o.lastIndexOf("%^")+2));
            }
        } else {
            // new component drag and drop
            type = o.trim();

            // get current id
//            int index = drawingCanvas.getCurrentIndex(type);
//
//            // preliminary name
//            gCmpName = type+index;
//
//            // check if name exists on the canvas
//            if(drawingCanvas.getGCMPs().containsKey(type+index)){
//                index = index+1;
//            }
//
//            // temporary name
//            gCmpName = type+index;

            // check to see if that id is taken already by stored components
            int index = CDesktop.assignUniqueId(type);

            // create the final name.
            gCmpName = type+index;

            gCmpImageFile = File.separator+"resources"+File.separator+type+".png";
            gc.setType(type);

            // assigning a unique ID to a new component. Note this is not a predefined component.
            gc.setId(CDesktop.assignUniqueId(type));

            gc.setPriority(ACodaType.getEnum(type).priority());


//            // store current index for the type.
//            drawingCanvas.setCurrentIndex(type, index+1);

            if(!CDesktop.defineRocMastership(gCmpName,type, gc)) {
                return;
            }

        }

        gc.setName(gCmpName);
        gc.setW(drawingCanvas.getW());
        gc.setH(drawingCanvas.getH());
        gc.setImage(drawingCanvas.createBufferedImage(gCmpImageFile));

        if(!typePositions.keySet().contains(type)){
            int gridSize2 = 120;
            Point p = new Point(currentX+= gridSize2,0);
            typePositions.put(type,p);
        } else {
            double x = typePositions.get(type).getX();
            double y = typePositions.get(type).getY();
            int yIncrement = 80;
            int xIncrement = 240;
            if(y>= yPage+(yIncrement *9)) {
                y = yPage - yIncrement;
                x = x+ xIncrement;

            }
            if(x>= (xIncrement)*10) {
                yPage = yPage+ (yIncrement *11);
                y =yPage- yIncrement;
                x = currentX;

            }
            typePositions.get(type).setLocation(x,y+ yIncrement);

        }
        gc.setX(typePositions.get(type).getX());
        gc.setY(typePositions.get(type).getY());

        // adding the default module to all components
        drawingCanvas.addgCmp(gc);
        drawingCanvas.setSelectedGCmpName(gc.getName());
        drawingCanvas.repaint();


        // persisting a type and id 04.24.17
        JCGSetup stp = JCGSetup.getInstance();
        File f = new File(stp.getCoolHome() +
                File.separator +
                stp.getExpid() + File.separator +
                "jcedit" + File.separator +
                gc.getType() + ".txt");
        if(!f.exists()){
            try {
                BufferedWriter bw = new BufferedWriter(new FileWriter(stp.getCoolHome() +
                        File.separator +
                        stp.getExpid() + File.separator +
                        "jcedit" + File.separator +
                        gc.getType() + ".txt"));
                bw.write(gc.getName() + "$" +
                        gc.getType() + "$" +
                        gc.getSubType() + "$" +
                        gc.getId() + "$" +
                        gc.getDescription() + "@@\n"
                );
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void clearPositionMap(){
        typePositions.clear();
        currentX    = -120;
    }

    // static methods


}
