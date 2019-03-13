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


import org.jlab.coda.cedit.forms.simple.SComponentForm;
import org.jlab.coda.cedit.forms.simple.SNLinkForm;
import org.jlab.coda.cedit.forms.simple.SOutputForm;
import org.jlab.coda.cedit.system.*;
import org.jlab.coda.cedit.system.JCGComponent;
import org.jlab.coda.cedit.system.JCGLink;
import org.jlab.coda.cedit.system.JCGTransport;

import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.geom.Rectangle2D;
import java.awt.geom.AffineTransform;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.AffineTransformOp;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.io.IOException;

public class DrawingCanvas extends JPanel {


    private final double GRIDSIZE = 40.0;
    private final double FONTSIZE = 16.0;
    private double gridSize = GRIDSIZE;
    private double currentGridSize = 0.0;
    private double x, y, w, h,_x, _y;
    private int prevX, prevY;
    private double px, py;

    private int x1;
    private int y1;

    // needed for dragging a component with a link (to draw end point of the link)
    private int xEndDrag;
    private int yEndDrag;

    private Rectangle2D rectangle;

    private Cursor curCursor;

    private Graphics2D g2D;

    private BufferedImage bufferedImage;

    private static ConcurrentHashMap<String,JCGComponent> GCMPs = new ConcurrentHashMap<>();

    private JCGComponent supervisor = new JCGComponent();

    private Map<String,JCoordinate> _unZoomGCMPs = new HashMap<String,JCoordinate>();

    private String selectedGCmpName = "undefined";
    public Set<JCGComponent> selectedGroup = new HashSet<>();
    private String selectedGroupType = "undefined";

    private boolean isMovable      = true;
    private boolean isAllowed2Move = true;

    private boolean isActiveGrid = false;
    private boolean isGridVisible = false;
    public boolean isGroupMode = false;

    private double zmv = 1.0;
    private AffineTransformOp affineTransportOp;
    private double fontSize = FONTSIZE;
    private Font font;

    private boolean isLinkMode = false;
    private boolean isBoxesOn = true;
    private double lineStartX, lineStartY, lineEndX, lineEndY;
    private String lineStartGc = "undefined", lineEndGc = "undefined";
    private String lineStartGcType = "undefined", lineEndGcType = "undefined";

    private ArrayList<String> sourceNetworkInfo = new ArrayList<String>();
    private ArrayList<String> destinationNetworkInfo = new ArrayList<String>();

    private DrawingCanvas me;

    private boolean isMousePressed = false;
    private boolean isEditable = true;

    public int    rocIndex    = 0;
    public int    dcIndex     = 0;
    public int    sebIndex    = 0;
    public int    pebIndex    = 0;
    public int    erIndex     = 0;
    public int    slcIndex    = 0;
    public int    fcsIndex    = 0;
    public int    usrIndex    = 0;
    public int    fileIndex   = 0;

    public DrawingCanvas(double w, double h) {
        this.w = w;
        this.h = h;
        setBackground(Color.white);
        setPreferredSize(new Dimension(3300, 3300));
        addMouseListener(new MyMouseListener());
        addMouseMotionListener(new MyMouseMotionListener());
        font = new Font("Serif", Font.PLAIN, (int)fontSize);
        zoomImage();
        me = this;

    }

    public DrawingCanvas(double w, double h, boolean isEditable) {
        this.w = w;
        this.h = h;
        this.isEditable = isEditable;
        setBackground(Color.white);
        setPreferredSize(new Dimension(3300, 3300));
        addMouseListener(new MyMouseListener());
        addMouseMotionListener(new MyMouseMotionListener());
        font = new Font("Serif", Font.PLAIN, (int)fontSize);
        zoomImage();
        me = this;

    }


    public JCGComponent getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(JCGComponent supervisor) {
        this.supervisor = supervisor;
    }


    public static JCGComponent getComp(String name){
        return GCMPs.get(name);
    }

    public void refresh(){
        drawGrid(gridSize);

        if(isGroupMode){
            for(JCGComponent gc:GCMPs.values()){
                if(gc.getName().equals(selectedGCmpName)){
                    if(selectedGroup.isEmpty()) {
                        g2D.setColor(Color.RED);
                    } else {
                        if (gc.getType().equals(selectedGroupType)) {
                            g2D.setColor(Color.RED);
                        } else {
                            g2D.setColor(Color.BLACK);
                        }
                    }
                } else {
                    if(selectedGroup.contains(gc)){
                        g2D.setColor(Color.RED);
                    } else {
                        g2D.setColor(Color.BLACK);
                    }
                }
                drawComponentAndLinks(gc);
            }
        } else {
            for(JCGComponent gc:GCMPs.values()){
                if(gc.getName().equals(selectedGCmpName)){
                    g2D.setColor(Color.RED);
                } else {
                    g2D.setColor(Color.BLACK);
                }
                drawComponentAndLinks(gc);
            }
        }
        if(isLinkMode){
            if(xEndDrag >0 && yEndDrag >0 && lineStartX>0 && lineStartY>0){
                g2D.drawLine((int)lineStartX, (int)lineStartY, xEndDrag, yEndDrag);
            }
        }
    }

    public void setGridVisible(boolean gridVisible) {
        isGridVisible = gridVisible;
        repaint();
    }

    public void align(){
        for(JCGComponent com:GCMPs.values()){
            com.setX(com.getGridX());
            com.setY(com.getGridY());
            for(JCGLink l:com.getLnks()){
                if(l.getDestinationComponentName().equals(com.getName())){
                    l.setEndX((int) com.getGridX());
                    l.setEndY((int) com.getGridY() + (int) h / 2);
                } else if(l.getSourceComponentName().equals(com.getName())){
                    l.setStartX((int)com.getGridX()+(int)w);
                    l.setStartY((int)com.getGridY()+(int)h/2);
                }
            }
        }
        repaint();
    }

    public ConcurrentHashMap<String, JCGComponent> getGCMPs() {
        return GCMPs;
    }

    public void dumpGCMPs(){
        for(JCGComponent com:GCMPs.values()){
            System.out.println(com);
        }
    }

    public double getGridSize() {
        return gridSize;
    }

    public void resetGridFont() {
        gridSize = GRIDSIZE;
        fontSize = FONTSIZE;
    }

    public void setAllowedToMove(boolean b){
        isAllowed2Move = b;
    }

    public boolean isAllowed2Move() {
        return isAllowed2Move;
    }

    public void gridOn(){
        isActiveGrid = true;
    }

    public void gridOff(){
        isActiveGrid = false;
    }

    public void groupOn(){
        isGroupMode = true;
        selectedGroup.clear();
        selectedGroupType = "undefined";
    }

    public void groupOff(){
        isGroupMode = false;
        selectedGroup.clear();
        selectedGroupType = "undefined";
        repaint();
    }

    public void groupReset(){
        groupOff();
        groupOn();
    }
    public void removeFromGroup(){
        selectedGroup.remove(GCMPs.get(selectedGCmpName));
        selectedGCmpName = "undefined";
        repaint();
    }


    public void linkModeOn(){
        isLinkMode = true;
    }
    public void linkModeOff(){
        isLinkMode = false;
    }

    public boolean isLinkMode() {
        return isLinkMode;
    }

    public void boxesOn(){
        isBoxesOn = true;
        repaint();
    }

    public void boxesOff(){
        isBoxesOn = false;
        repaint();
    }

    public double getW() {
        return w;
    }

    public double getH() {
        return h;
    }

    public void zoomOut(){
        if(zmv>=0.25){
            zmv = zmv - 0.25;
            zoomImage();
            if(zmv!=1.0){
                zoom_out(0.25,false);
            } else {
                zoom_out(0.25,true);
            }
            repaint();
        }

    }

    public void zoomIn(){
        if(zmv<=2.0){
            zmv = zmv + 0.25;
            zoomImage();
            if(zmv!=1.0){
                zoom_in(0.25, false);
            } else {
                zoom_in(0.25, true);
            }
            repaint();
        }
    }


    public void unZoom(){
        if(zmv>1){
            while(zmv!=1 ){
                zoomOut();
            }
        } else if(zmv<1){
            while(zmv!=1){
                zoomIn();
            }
        }
    }

    public String getSelectedGCmpName() {
        return selectedGCmpName;
    }

    public void setSelectedGCmpName(String selectedGCmpName) {
        this.selectedGCmpName = selectedGCmpName;
        repaint();
    }

    private void zoomImage(){
        if(zmv>0){
            AffineTransform scl = new AffineTransform();
            scl.scale(zmv, zmv);
            affineTransportOp = new AffineTransformOp(scl, AffineTransformOp.TYPE_BILINEAR);
        }
    }


    private void zoom_out(double zmv, boolean reset){
        if(zmv>0){
            if(reset){
                resetGridFont();
                font = new Font("Serif", Font.PLAIN, (int)fontSize);
                for(JCGComponent gc:GCMPs.values()){
                    if(_unZoomGCMPs.containsKey(gc.getName())) {
                        JCoordinate c = _unZoomGCMPs.get(gc.getName());

                        gc.setX(c.getX());
                        gc.setY(c.getY());
                        gc.setPx(c.getPx());
                        gc.setPy(c.getPy());
                        gc.setW(c.getW());
                        gc.setH(c.getH());

                        for(JCGLink l:gc.getLnks()){
                            if(l.getDestinationComponentName().equals(gc.getName())){
                                l.setEndX(l.getEndX() + (l.getEndX()*zmv));
                                l.setEndY(l.getEndY() + (l.getEndY()*zmv));
                            } else if(l.getSourceComponentName().equals(gc.getName())){
                                l.setStartX(l.getStartX() + (l.getStartX()*zmv));
                                l.setStartY(l.getStartY() + (l.getStartY()*zmv));
                            }
                        }
                    }
                }
            } else {
                gridSize = gridSize - gridSize*zmv;
                fontSize = fontSize - fontSize*zmv;
                font = new Font("Serif", Font.PLAIN, (int)fontSize);
//            x= x - (x*zmv);
//            y= y - (y*zmv);
//            w= w - (w*zmv);
//            h= h - (h*zmv);
//            px= px - (px*zmv);
//            py= py - (py*zmv);
                for(JCGComponent gc:GCMPs.values()){

                    double x = gc.getX();
                    double y = gc.getY();
                    double w = gc.getW();
                    double h = gc.getH();
                    double px = gc.getPx();
                    double py = gc.getPy();
                    gc.setX( x - (x*zmv));
                    gc.setY( y - (y*zmv));
                    gc.setPx( px - (px*zmv));
                    gc.setPy( py - (py*zmv));
                    gc.setW( w - (w*zmv));
                    gc.setH( h - (h*zmv));

                    for(JCGLink l:gc.getLnks()){
                        if(l.getDestinationComponentName().equals(gc.getName())){
                            l.setEndX(l.getEndX() - (l.getEndX()*zmv));
                            l.setEndY(l.getEndY() - (l.getEndY()*zmv));
                        } else if(l.getSourceComponentName().equals(gc.getName())){
                            l.setStartX(l.getStartX() - (l.getStartX()*zmv));
                            l.setStartY(l.getStartY() - (l.getStartY()*zmv));
                        }
                    }
                }
            }
            clearCanvas();
            repaint();
        }
    }

    private void zoom_in(double zmv, boolean reset){
        if(zmv>0){
            if(reset){
                resetGridFont();
                font = new Font("Serif", Font.PLAIN, (int)fontSize);
                for(JCGComponent gc:GCMPs.values()){
                    if(_unZoomGCMPs.containsKey(gc.getName())) {
                        JCoordinate c = _unZoomGCMPs.get(gc.getName());

                        gc.setX(c.getX());
                        gc.setY(c.getY());
                        gc.setPx(c.getPx());
                        gc.setPy(c.getPy());
                        gc.setW(c.getW());
                        gc.setH(c.getH());

                        for(JCGLink l:gc.getLnks()){
                            if(l.getDestinationComponentName().equals(gc.getName())){
                                l.setEndX(l.getEndX() + (l.getEndX()*zmv));
                                l.setEndY(l.getEndY() + (l.getEndY()*zmv));
                            } else if(l.getSourceComponentName().equals(gc.getName())){
                                l.setStartX(l.getStartX() + (l.getStartX()*zmv));
                                l.setStartY(l.getStartY() + (l.getStartY()*zmv));
                            }
                        }
                    }
                }
            }else {
                gridSize = gridSize + gridSize*zmv;
                fontSize = fontSize + fontSize*zmv;
                font = new Font("Serif", Font.PLAIN, (int)fontSize);
//            x= x + (x*zmv);
//            y= y + (y*zmv);
//            w= w + (w*zmv);
//            h= h + (h*zmv);
//            px= px + (px*zmv);
//            py= py + (py*zmv);
                for(JCGComponent gc:GCMPs.values()){

                    double x = gc.getX();
                    double y = gc.getY();
                    double w = gc.getW();
                    double h = gc.getH();
                    double px = gc.getPx();
                    double py = gc.getPy();
                    gc.setX( x + (x*zmv));
                    gc.setY( y + (y*zmv));
                    gc.setPx( px + (px*zmv));
                    gc.setPy( py + (py*zmv));
                    gc.setW( w + (w*zmv));
                    gc.setH( h + (h*zmv));

                    for(JCGLink l:gc.getLnks()){
                        if(l.getDestinationComponentName().equals(gc.getName())){
                            l.setEndX(l.getEndX() + (l.getEndX()*zmv));
                            l.setEndY(l.getEndY() + (l.getEndY()*zmv));
                        } else if(l.getSourceComponentName().equals(gc.getName())){
                            l.setStartX(l.getStartX() + (l.getStartX()*zmv));
                            l.setStartY(l.getStartY() + (l.getStartY()*zmv));
                        }
                    }
                }
            }
            clearCanvas();
            repaint();
        }
    }



    public void addgCmp(JCGComponent gc){
        GCMPs.put(gc.getName(),gc);
        x = gc.getX();
        y = gc.getY();
        w = gc.getW();
        h = gc.getH();
        bufferedImage = gc.getImage();
        px = gc.getPx();
        py = gc.getPy();

        JCoordinate cor = new JCoordinate();
        cor.setX(x);
        cor.setY(y);
        cor.setW(w);
        cor.setH(h);
        cor.setPx(px);
        cor.setPy(py);
        _unZoomGCMPs.put(gc.getName(),cor);

        drawGComponent(gc.getName(), gc.getX(), gc.getY(), gc.getW(), gc.getH());
    }
    public void addCmp(JCGComponent gc){
        GCMPs.put(gc.getName(), gc);
    }

    public void removeCmp(JCGComponent gc){
        if(GCMPs.containsKey(gc.getName())){
            JCGComponent c = GCMPs.get(gc.getName());
            GCMPs.remove(gc.getName(),c);
        }
        _unZoomGCMPs.remove(gc.getName());
    }

    public void clearGCMPs(){
        GCMPs.clear();
    }

    public JCGComponent defineGCmp(){
        for(JCGComponent gc:GCMPs.values()){
            if(x1>gc.getX() && x1<(gc.getX()+gc.getW()) && y1>gc.getY() && y1<(gc.getY()+gc.getH())){
                selectedGCmpName = gc.getName();
                bufferedImage = gc.getImage();
                x = gc.getX();
                y = gc.getY();
                px = gc.getPx();
                py = gc.getPy();
                return gc;
            }
        }
        return null;
    }


    private void defineLineStartXY(double x, double y){
        for(JCGComponent gc:GCMPs.values()){
            if(x>gc.getX() && x<(gc.getX()+gc.getW()) && y>gc.getY() && y<(gc.getY()+gc.getH())){
                lineStartX = gc.getX()+gc.getW();
                lineStartY = gc.getY()+gc.getH()/2;
                lineStartGc = gc.getName();
                lineStartGcType = gc.getType();
                selectedGCmpName = lineStartGc;
                return;
            } else {
                lineStartX = 0;
                lineStartY = 0;
                lineStartGc = "undefined";
            }
        }
    }

    private void defineLineEndXY(double x, double y){
        for(JCGComponent gc:GCMPs.values()){
            if(x>gc.getX() && x<(gc.getX()+gc.getW()) && y>gc.getY() && y<(gc.getY()+gc.getH())){
                lineEndX = gc.getX();
                lineEndY = gc.getY()+gc.getH()/2;
                lineEndGc = gc.getName();
                lineEndGcType = gc.getType();
                return;
            } else {
                lineEndX = 0;
                lineEndY = 0;
                lineEndGc = "undefined";
            }
        }
    }

    private boolean isLinkAllowed(String startType, String endType){
        boolean res = true;
        switch (ACodaType.getEnum(startType)) {
            case ROC:
            case GT:
            case TS:
                res = endType.equals(ACodaType.PEB.name()) ||
                        endType.equals(ACodaType.DC.name()) ||
                        endType.equals(ACodaType.EB.name()) ||
                        endType.equals(ACodaType.FILE.name());
                break;
            case DC:
                res = endType.equals(ACodaType.SEB.name()) ||
                        endType.equals(ACodaType.FILE.name());
                break;
            case EB:
            case PEB:
            case SEB:
                res = endType.equals(ACodaType.ER.name()) ||
                        endType.equals(ACodaType.FCS.name()) ||
                        endType.equals(ACodaType.FILE.name())||
                        endType.equals(ACodaType.USR.name());
                break;
            case FCS:
                res = endType.equals(ACodaType.ER.name()) ||
                        endType.equals(ACodaType.FILE.name());
                break;
            case ER:
                res = endType.equals(ACodaType.FILE.name()) ||
                        endType.equals(ACodaType.USR.name());
                break;
        }

        return res;

    }

    private void connectGComponents(){
        if((int)lineStartX>0 &&
                (int)lineStartY> 0 &&
                (int)lineEndX> 0 &&
                (int)lineEndY >0 &&
                !lineStartGc.equals(lineEndGc) &&
                !lineStartGc.equals("undefined") &&
                !lineEndGc.equals("undefined") &&
                isLinkAllowed(lineStartGcType, lineEndGcType) &&
                !GCMPs.isEmpty())
        {

            JCGLink gle = null;
            for(JCGLink l:GCMPs.get(lineEndGc).getLnks()){
                if(l.getDestinationComponentName().equals(lineEndGc) && l.getSourceComponentName().equals(lineStartGc)){
                    gle = l;
                    break;
                }
            }
            if(gle!=null){
                gle.setStartX((int) lineStartX);
                gle.setStartY((int) lineStartY);
                gle.setEndX((int) lineEndX);
                gle.setEndY((int) lineEndY);

                new SNLinkForm(me, gle,isEditable).setVisible(true);
            } else {
                gle = new JCGLink();
                gle.setName(lineStartGc + "_" + lineEndGc);
                gle.setStartX((int) lineStartX);
                gle.setStartY((int) lineStartY);
                gle.setEndX((int)lineEndX);
                gle.setEndY((int)lineEndY);
                gle.setSourceComponentName(GCMPs.get(lineStartGc).getName());
                gle.setSourceComponentType(GCMPs.get(lineStartGc).getType());
                gle.setDestinationComponentName(GCMPs.get(lineEndGc).getName());
                gle.setDestinationComponentType(GCMPs.get(lineEndGc).getType());
                gle.setSourceModuleName(GCMPs.get(lineStartGc).getModule().getName());
                gle.setDestinationModuleName(GCMPs.get(lineEndGc).getModule().getName());
                new SNLinkForm(me, gle,isEditable).setVisible(true);
            }
        } else {
//            JOptionPane.showMessageDialog(null,"Link not allowed.", "COOL Warning",JOptionPane.WARNING_MESSAGE);
        }
//        linkModeOff();
    }


    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g2D = (Graphics2D) g;
        g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2D.setFont(font);
        if (curCursor != null)
            setCursor(curCursor);
        refresh();
    }

    public BufferedImage createBufferedImage(String fileName){

//        File imageFile = new File(fileName);
        try{
            bufferedImage = ImageIO.read(getClass().getResource(fileName));
        }catch(IOException | IllegalArgumentException e ) {
            System.out.println("Error loading "+fileName);
            System.out.println(e.toString());
        }
        return bufferedImage;
    }

    public void drawGrid(double gSize){
        if(isGridVisible){
            Color c = g2D.getColor();
            if(gSize<=0){
                return;
            } else if(gSize<1){
                gSize = 1.0;
            }
            g2D.setColor(Color.LIGHT_GRAY);
            for(int i=0;i<getWidth();i=i+(int)gSize){
                g2D.drawLine(i,0,i,getHeight());
            }
            for(int i=0;i<getHeight();i=i+(int)gSize){
                g2D.drawLine(0,i,getWidth(),i);
            }
            g2D.setColor(c);
        }
    }

    /**
     * Draws the image on a Graphics2D context.
     * @param x x of the image
     * @param y y of the image
     */

    private void drawImage(String compName, double x,double y){
        JCGComponent cmp = GCMPs.get(compName);
        if(cmp==null ){
            System.out.println("Severe Error: null component. "+ compName);
            return;
        }
        cmp.setImage(createBufferedImage(File.separator + "resources" + File.separator + cmp.getType() + ".png"));
        if(cmp.getImage()==null){
            System.out.println("Severe Error: can not create component image. "+ compName);
            return;
        }
        BufferedImage bi = affineTransportOp.filter(cmp.getImage(), null);
        g2D.drawRenderedImage(bi, AffineTransform.getTranslateInstance(x, y));
    }


    public void drawGComponent(String compName, double x, double y, double w, double h){
        if(isBoxesOn){
            rectangle = new Rectangle2D.Double(x, y, w, h);
            g2D.draw(rectangle);
        }
        g2D.drawString(compName, (int) x + 2, (int) (y + h - 2));

        if(compName.equals(selectedGCmpName)){
            if(sourceNetworkInfo!=null && !sourceNetworkInfo.isEmpty()){
                int ly = 0;
                g2D.setFont(new Font("Serif", Font.PLAIN, (int)fontSize-4));
                for(String s:sourceNetworkInfo) {
                    g2D.drawString(s, (int)((x-60*zmv)), (int)((y+ly)));
                    ly+=20*zmv;
                }
                g2D.setFont(font);
            }
            if(destinationNetworkInfo!=null && !destinationNetworkInfo.isEmpty()){
                int ly = 0;
                g2D.setFont(new Font("Serif", Font.PLAIN, (int)fontSize-4));
                for(String s:destinationNetworkInfo) {
                    g2D.drawString(s, (int)((x+100*zmv)), (int)((y+ly)));
                    ly+=20*zmv;
                }
                g2D.setFont(font);
            }
        }

        drawImage(compName, x, y);
    }

    public void drawComponentAndLinks(JCGComponent gc){
        drawGComponent(gc.getName(), gc.getX(), gc.getY(), gc.getW(), gc.getH());


        for(JCGLink l:gc.getLnks()){
            double sx = 0.0;
            double sy = 0.0;
            double ex = 0.0;
            double ey = 0.0;
            // we must check if the components has been removed before drawing the link
            JCGComponent s = GCMPs.get(l.getSourceComponentName());
            if(s!=null){
                sx  = s.getX()+s.getW();
                sy  = s.getY()+s.getH()/2;
            }
            JCGComponent d = GCMPs.get(l.getDestinationComponentName());
            if(d!=null){
                ex  = d.getX();
                ey  = d.getY()+gc.getH()/2;
            }
            if(sx>0 && sy>0 && ex>0 && ey>0){
                g2D.drawLine((int) sx, (int) sy, (int) ex, (int) ey);
            }
        }
    }

    public void componentDelete(){
        if(!isLinkMode && !selectedGCmpName.equals("undefined")){
            g2D.setColor(g2D.getBackground());
            g2D.fill(new Rectangle.Double(x - 1, y - 1, w + 2, h + 2));
            g2D.setColor(Color.black);
            if(isGroupMode){
                for(JCGComponent c:selectedGroup){
                    _deleteComponent(c);
                    GCMPs.remove(c.getName());
                }
                groupReset();
            } else {
                JCGComponent c = GCMPs.get(selectedGCmpName);
                _deleteComponent(c);
                GCMPs.remove(selectedGCmpName);
            }
            repaint();
        }
    }

    private void _deleteComponent(JCGComponent c){
        // recycle deleted component id
        for(JCGLink l:c.getLnks()){
            if(l.getSourceComponentName().equals(c.getName())){

                JCGComponent dc = GCMPs.get(l.getDestinationComponentName());
                ArrayList<JCGLink> removedLinks = new ArrayList<JCGLink>();

                for(JCGLink dl:dc.getLnks()){
                    if(dl.getSourceComponentName().equals(c.getName()) ||
                            dl.getDestinationComponentName().equals(c.getName())){
                        removedLinks.add(dl);
                    }
                }
                for(JCGLink rl:removedLinks){
                    dc.removeLnk(rl);
                }
                GCMPs.put(dc.getName(),dc);
            }
            if(l.getDestinationComponentName().equals(c.getName())){
                JCGComponent dc = GCMPs.get(l.getSourceComponentName());
                ArrayList<JCGLink> removedLinks = new ArrayList<JCGLink>();
                for(JCGLink dl:dc.getLnks()){
                    if(dl.getSourceComponentName().equals(c.getName()) ||
                            dl.getDestinationComponentName().equals(c.getName())){
                        removedLinks.add(dl);
                    }
                }
                for(JCGLink rl:removedLinks){
                    dc.removeLnk(rl);
                }
                GCMPs.put(dc.getName(),dc);
            }
        }

    }
    public void removeAll(){
        for(JCGComponent c:GCMPs.values()){
            if(g2D != null) {
                g2D.setColor(g2D.getBackground());
                g2D.fill(new Rectangle.Double(c.getX() - 1, c.getY() - 1, c.getW() + 2, c.getH() + 2));
                g2D.setColor(Color.black);
                c.removeLnks();
            }
            GCMPs.remove(c.getName());
        }
        repaint();
    }


    public void linkDeleteAll(){
        ArrayList<JCGLink> dl = new ArrayList<JCGLink>();
        for(JCGComponent c:GCMPs.values()){

            ArrayList<JCGLink> removedLinks = new ArrayList<JCGLink>();

            for(JCGLink l:c.getLnks()){
                if(l.getSourceComponentName().equals(selectedGCmpName)){
                    removedLinks.add(l);
                    if(!l.getDestinationComponentName().equals("")){
                        dl.add(l);
                    }
                }
            }
            for(JCGLink rl:removedLinks){
                c.removeLnk(rl);
            }

        }
        if(!dl.isEmpty()){
            for(JCGLink l:dl){
                JCGComponent dcom = GCMPs.get(l.getDestinationComponentName());
                if(dcom!=null){
                    for(JCGLink ddl:dcom.getLnks()){
                        if(ddl.getName().equals(l.getName())){
                            dcom.removeLnk(ddl);
                            break;
                        }
                    }
                }
            }
        }
        repaint();
    }

    private void lkd(JCGComponent c, String lName){
        String destCompLink = "undefined";
        JCGLink rl = null;
        for(JCGLink l:c.getLnks()){
            if(l.getName().equals(lName)){
                destCompLink = l.getName();
                rl = l;
                break;
            }
        }
        if(rl!=null){
            c.removeLnk(rl);
        }

        rl = null;
        if(!destCompLink.equals("undefined")){
            JCGComponent dcom = GCMPs.get(destCompLink);
            if(dcom!=null){
                for(JCGLink dl:dcom.getLnks()){
                    if(dl.getName().equals(destCompLink)){
                        rl = dl;
                        break;
                    }
                }
                if(rl!=null){
                    dcom.removeLnk(rl);
                }
            }
        }
    }

    public void linkDelete(String lName){
        for(JCGComponent c:GCMPs.values()){
            lkd(c, lName);
        }
        repaint();
    }

    public void linkDelete2(String compName){
        for(JCGComponent c:GCMPs.values()){

            ArrayList<JCGLink> links2remove = new ArrayList<JCGLink>();
            ArrayList<JCGTransport> transports2remove = new ArrayList<JCGTransport>();
            ArrayList<String> componentLinks2remove = new ArrayList<String>();

            for(JCGLink l:c.getLnks()){
                if(l.getName().contains(compName)){
                    links2remove.add(l);
                    JCGComponent dcom = GCMPs.get(l.getDestinationComponentName());
                    for(JCGLink dl:dcom.getLnks()){
                        if(dl.getName().contains(l.getName())){
                            componentLinks2remove.add(dl.getName());
                            break;
                        }
                    }
                }
            }

            for(String ll:componentLinks2remove){
                linkDelete(ll);
            }
            for(JCGTransport t:c.getTrnsports()){
                if(t.getName().contains(compName)){
                    transports2remove.add(t);
                }
            }
            for(JCGLink l:links2remove){
                c.removeLnk(l);
            }
            for(JCGTransport t:transports2remove){
                c.removeTrnsport(t);
            }
        }

        repaint();
    }

    public String[] getLinkNames(){
        ArrayList<String> al = new ArrayList<String>();
        for(JCGLink l:GCMPs.get(selectedGCmpName).getLnks()){
            if(l.getSourceComponentName().equals(selectedGCmpName)){
                al.add(l.getName());
            }
        }
        if (!al.isEmpty()){
            return al.toArray(new String[al.size()]);
        } else {
            return null;
        }
    }

    public int getLinkCount(){
        int lc = 0;
        if(!selectedGCmpName.equals("undefined")){
            for(JCGLink l:GCMPs.get(selectedGCmpName).getLnks()){
                if(l.getSourceComponentName().equals(selectedGCmpName)){
                    lc++;
                }
            }
        }
        return lc;
    }

    public void clearCanvas(){
        g2D.setColor(g2D.getBackground());
        g2D.fill(new Rectangle.Double(getX(), getY(), getWidth(), getHeight()));
        g2D.setColor(Color.black);
    }


    public boolean samePosition(){
        return px == x && py == y;
    }

    public void drawHighlightSquares(Graphics2D g2D, Rectangle2D r) {
        double x = r.getX();
        double y = r.getY();
        double w = r.getWidth();
        double h = r.getHeight();
        g2D.setColor(Color.black);

        g2D.fill(new Rectangle.Double(x - 3.0, y - 3.0, 6.0, 6.0));

        g2D.fill(new Rectangle.Double(x + w * 0.5 - 3.0, y - 3.0, 6.0,6.0));

        g2D.fill(new Rectangle.Double(x + w - 3.0, y - 3.0, 6.0, 6.0));

        g2D.fill(new Rectangle.Double(x - 3.0, y + h * 0.5 - 3.0, 6.0,6.0));

        g2D.fill(new Rectangle.Double(x + w - 3.0, y + h * 0.5 - 3.0, 6.0,6.0));

        g2D.fill(new Rectangle.Double(x - 3.0, y + h - 3.0, 6.0, 6.0));

        g2D.fill(new Rectangle.Double(x + w * 0.5 - 3.0, y + h - 3.0, 6.0,6.0));

        g2D.fill(new Rectangle.Double(x + w - 3.0, y + h - 3.0, 6.0, 6.0));
    }


    class MyMouseListener extends MouseAdapter {

        public void mousePressed(MouseEvent e) {
            isMousePressed = true;
            if(isLinkMode){
                defineLineStartXY(e.getX(), e.getY());
                prevX = (int)lineStartX;
                prevY = (int)lineStartY;
            } else {
                sourceNetworkInfo.clear();
                destinationNetworkInfo.clear();
                x1 = e.getX();
                y1 = e.getY();
                JCGComponent cmp = defineGCmp();
                isMovable = cmp != null;
//                if(isMovable && isAllowed2Move){
                if(isMovable){
                    JCGComponent gc = defineGCmp();
                    if(gc!=null){
                        if(gc.getLnks().isEmpty()){
                            destinationNetworkInfo.add("To");
                            destinationNetworkInfo.add("None");
                        } else {
                            for(JCGLink l:gc.getLnks()){
                                if(l.getSourceComponentName().equals(gc.getName())){
                                    if(!destinationNetworkInfo.contains("To")) {
                                        destinationNetworkInfo.add("To");
                                    } else {
                                        destinationNetworkInfo.add("");
                                    }
//todo show more information about the transport
                                    if(GCMPs.containsKey(l.getDestinationComponentName())){
                                        for(JCGTransport tr:GCMPs.get(l.getDestinationComponentName()).getTrnsports()){
                                            if(tr.getName().equals(l.getDestinationTransportName())){
                                                destinationNetworkInfo.add(tr.getTransClass());
                                                if(tr.getTransClass().equals("Et")){
                                                    destinationNetworkInfo.add(tr.getEtName());
                                                    destinationNetworkInfo.add(tr.getEtMethodCon());
                                                } else if(tr.getTransClass().equals("File")){
                                                    destinationNetworkInfo.add(tr.getFileName());
                                                    destinationNetworkInfo.add(tr.getFileType());
                                                }
                                                break;
                                            }
                                        }
                                    }
                                } else if (l.getDestinationComponentName().equals(gc.getName())){
                                    if(!sourceNetworkInfo.contains("From")) {
                                        sourceNetworkInfo.add("From");
                                    }
//todo show more information about the transport
                                    boolean noSourceTransport = true;
                                    if(GCMPs.containsKey(l.getSourceComponentName())){
                                        for(JCGTransport tr:GCMPs.get(l.getSourceComponentName()).getTrnsports()){
                                            if(tr.getName().equals(l.getSourceTransportName())){
                                                sourceNetworkInfo.add(l.getSourceComponentName());
                                                noSourceTransport = false;
                                                break;
                                            }
                                        }
                                        if(noSourceTransport){
                                            sourceNetworkInfo.add(l.getSourceComponentName());
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            me.repaint();
        }

        public void mouseReleased(MouseEvent e) {
            isMousePressed = false;
            xEndDrag = 0;
            yEndDrag = 0;
            if(isLinkMode){
                defineLineEndXY(e.getX(), e.getY());
                connectGComponents();
            } else {
                // clear link info
                int sl = sourceNetworkInfo.size();
                int dl = destinationNetworkInfo.size();
                sourceNetworkInfo.clear();
                destinationNetworkInfo.clear();
                for(int i= 0; i<sl;i++){
                    sourceNetworkInfo.add("");
                }
                for(int i= 0; i<dl;i++){
                    destinationNetworkInfo.add("");
                }
            }
            me.repaint();
        }

        public void mouseClicked(MouseEvent e) {
            if(isMovable && !isLinkMode) {
                if(e.getClickCount()>=2){
                    JCGComponent gc = defineGCmp();
                    if(gc!=null){
                        if(gc.getType().equals(ACodaType.FILE.name())){
                            JCGLink gle = null;
                            for(JCGLink l:GCMPs.get(gc.getName()).getLnks()){
                                if(l.getDestinationComponentName().equals(gc.getName())){
                                    gle = l;
                                    break;
                                }
                            }
                            if(gle!=null){
                                new SOutputForm(me,gc,gle,isEditable).setVisible(true);
                            } else {
                                new SOutputForm(me,gc,isEditable).setVisible(true);
                            }
//                        else if (gc.getType().equals(ACodaType.FCS.name())){
//                            new FCSForm(me,gc,isEditable);
//                        }
                        }  else {
                            new SComponentForm(me,gc,isEditable);
                        }
                    } else {
                        System.out.println("Error: gc = null");
                    }
                } else {
                    // single click if it is group mode will add to the list of groped components
                    if(isGroupMode){
                        JCGComponent gc = defineGCmp();
                        if(gc!=null){
                            if(selectedGroup.isEmpty()){
                                selectedGroupType = gc.getType();
                            }
                            if (gc.getType().equals(selectedGroupType)){
                                selectedGroup.add(gc);
                            }
                        }
                    }

                }
            }
        }

    }

    class MyMouseMotionListener extends MouseMotionAdapter {

        public void mouseDragged(MouseEvent e) {
            if (!isLinkMode && isMovable && isAllowed2Move && e.getX()>0 && e.getY()>0) {
                int x2 = e.getX();
                int y2 = e.getY();
                x = x + x2 - x1;
                y = y + y2 - y1;
                x1 = x2;
                y1 = y2;
                if(GCMPs.containsKey(selectedGCmpName)){
                    _x = (int)(x/gridSize)*gridSize;
                    _y = (int)(y/gridSize)*gridSize;
                    if(isActiveGrid){
                        GCMPs.get(selectedGCmpName).setX(_x);
                        GCMPs.get(selectedGCmpName).setY(_y);
                        GCMPs.get(selectedGCmpName).setGridX(_x);
                        GCMPs.get(selectedGCmpName).setGridY(_y);

                        _unZoomGCMPs.get(selectedGCmpName).setX(_x);
                        _unZoomGCMPs.get(selectedGCmpName).setY(_y);

                        for(JCGLink l:GCMPs.get(selectedGCmpName).getLnks()){
                            if(l.getDestinationComponentName().equals(selectedGCmpName)){
                                l.setEndX((int) _x);
                                l.setEndY((int) _y + (int) h / 2);
                            } else if(l.getSourceComponentName().equals(selectedGCmpName)){
                                l.setStartX((int)_x+(int)w);
                                l.setStartY((int)_y+(int)h/2);
                            }
                        }
                    } else {
                        GCMPs.get(selectedGCmpName).setX(x);
                        GCMPs.get(selectedGCmpName).setY(y);
                        GCMPs.get(selectedGCmpName).setGridX(_x);
                        GCMPs.get(selectedGCmpName).setGridY(_y);

                        _unZoomGCMPs.get(selectedGCmpName).setX(_x);
                        _unZoomGCMPs.get(selectedGCmpName).setY(_y);

                        for(JCGLink l:GCMPs.get(selectedGCmpName).getLnks()){
                            if(l.getDestinationComponentName().equals(selectedGCmpName)){
                                l.setEndX((int) x);
                                l.setEndY((int) y + (int) h / 2);
                            } else if(l.getSourceComponentName().equals(selectedGCmpName)){
                                l.setStartX((int)x+(int)w);
                                l.setStartY((int)y+(int)h/2);
                            }
                        }
                    }
                }
            } else if (isLinkMode) {
                xEndDrag = e.getX();
                yEndDrag = e.getY();
            }
            me.repaint();
        }

        public void mouseMoved(MouseEvent e) {
            if(!isLinkMode && isMovable && isAllowed2Move && e.getX()>0 && e.getY()>0 && rectangle != null){
                if (rectangle.contains(e.getX(), e.getY())) {
                    curCursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
                } else {
                    curCursor = Cursor.getDefaultCursor();
                }
            }
        }

    }

}