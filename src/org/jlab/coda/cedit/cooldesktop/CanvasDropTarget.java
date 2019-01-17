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

import org.jlab.coda.cedit.system.ACodaType;
import org.jlab.coda.cedit.system.JCGSetup;

import javax.swing.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.IOException;


public abstract class CanvasDropTarget implements DropTargetListener {

    private boolean         debug = false;
    protected JPanel        pane;
    protected DropTarget    dropTarget;
    protected boolean       acceptableType; // Indicates whether data is acceptable
    protected DataFlavor    targetFlavor; // Flavor to use for transfer

    private JCGSetup stp = JCGSetup.getInstance();

    public CanvasDropTarget(JPanel pane) {
        this.pane = pane;

        // Create the DropTarget and register it with the JPanel.
        dropTarget = new DropTarget(pane, DnDConstants.ACTION_COPY_OR_MOVE,
                this, true, null);
    }

    public abstract void dropAction(String s);

    // Implementation of the DropTargetListener interface
    public void dragEnter(DropTargetDragEvent dtde) {
        if(debug) System.out.println("dragEnter, drop action = "
                + showActions(dtde.getDropAction()));

        // Get the type of object being transferred and determine
        // whether it is appropriate.
        checkTransferType(dtde);

        // Accept or reject the drag.
        acceptOrRejectDrag(dtde);
    }

    public void dragExit(DropTargetEvent dte) {
        if(debug) System.out.println("DropTarget dragExit");
    }

    public void dragOver(DropTargetDragEvent dtde) {
        if(debug) System.out.println("DropTarget dragOver, drop action = "
                + showActions(dtde.getDropAction()));

        // Accept or reject the drag
        acceptOrRejectDrag(dtde);
    }

    public void dropActionChanged(DropTargetDragEvent dtde) {
        if(debug) System.out.println("DropTarget dropActionChanged, drop action = "
                + showActions(dtde.getDropAction()));

        // Accept or reject the drag
        acceptOrRejectDrag(dtde);
    }

    public void drop(DropTargetDropEvent dtde) {
        if(debug) System.out.println("DropTarget drop, drop action = "
                + showActions(dtde.getDropAction()));

        // Check the drop action
        if ((dtde.getDropAction() & DnDConstants.ACTION_COPY_OR_MOVE) != 0) {
            // Accept the drop and get the transfer data
            dtde.acceptDrop(dtde.getDropAction());
            Transferable transferable = dtde.getTransferable();

            try {
                boolean result = dropComponent(transferable);

                dtde.dropComplete(result);
                if(debug) System.out.println("Drop completed, success: " + result);
            } catch (Exception e) {
                if(debug) System.out.println("Exception while handling drop " + e);
                dtde.dropComplete(false);
            }
        } else {
            if(debug) System.out.println("Drop target rejected drop");
            dtde.rejectDrop();
        }
    }

    // Internal methods start here

    protected boolean acceptOrRejectDrag(DropTargetDragEvent dtde) {
        int dropAction = dtde.getDropAction();
        int sourceActions = dtde.getSourceActions();
        boolean acceptedDrag = false;

        if(debug) System.out.println("\tSource actions are "
                + showActions(sourceActions) + ", drop action is "
                + showActions(dropAction));

        // Reject if the object being transferred
        // or the operations available are not acceptable.
        if (!acceptableType
                || (sourceActions & DnDConstants.ACTION_COPY_OR_MOVE) == 0) {
            if(debug) System.out.println("Drop target rejecting drag");
            dtde.rejectDrag();
        } else if ((dropAction & DnDConstants.ACTION_COPY_OR_MOVE) == 0) {
            // Not offering copy or move - suggest a copy
            if(debug) System.out.println("Drop target offering COPY");
            dtde.acceptDrag(DnDConstants.ACTION_COPY);
            acceptedDrag = true;
        } else {
            // Offering an acceptable operation: accept
            if(debug) System.out.println("Drop target accepting drag");
            dtde.acceptDrag(dropAction);
            acceptedDrag = true;
        }

        return acceptedDrag;
    }

    protected void checkTransferType(DropTargetDragEvent dtde) {
        // Only accept a flavor that returns a String component
        acceptableType = false;
        DataFlavor[] fl = dtde.getCurrentDataFlavors();
        for (DataFlavor aFl : fl) {
            Class dataClass = aFl.getRepresentationClass();
            // This flavor returns a String - accept it.
            if (dataClass.getCanonicalName().equals("java.lang.String") ||
                    dataClass.getCanonicalName().equals("java.lang.Integer")) {
                targetFlavor = aFl;
                acceptableType = true;
                break;
            }
        }

        if(debug) System.out.println("File type acceptable - " + acceptableType);
    }

    protected boolean dropComponent(Transferable transferable)
            throws IOException, UnsupportedFlavorException {
        Object o = transferable.getTransferData(targetFlavor);
        if (o instanceof String) {
            String s = (String)o;
            if(!stp.isInteger(s)){
                dropAction(s);
                return true;
            } else {
                Integer ind = Integer.parseInt(s);
                switch(ind) {
                    case 0:
                        dropAction(ACodaType.TS.name());
                        break;
                    case 2:
                        dropAction(ACodaType.ROC.name());
                        break;
                    case 1:
                        dropAction(ACodaType.GT.name());
                        break;
                    case 3:
                        dropAction(ACodaType.DC.name());
                        break;
                    case 4:
                        dropAction(ACodaType.PEB.name());
                        break;
                    case 5:
                        dropAction(ACodaType.SEB.name());
                        break;
                    case 6:
                        dropAction(ACodaType.FCS.name());
                        break;
                    case 7:
                        dropAction(ACodaType.ER.name());
                        break;
                    case 8:
                        dropAction(ACodaType.SLC.name());
                        break;
                    case 9:
                        dropAction(ACodaType.USR.name());
                        break;
                    case 10:
                        dropAction(ACodaType.FILE.name());
                        break;
                }
                return  true;
            }
        }
        return false;
    }

    public String showActions(int action) {
        String actions = "";
        if ((action & (DnDConstants.ACTION_LINK | DnDConstants.ACTION_COPY_OR_MOVE)) == 0) {
            return "None";
        }

        if ((action & DnDConstants.ACTION_COPY) != 0) {
            actions += "Copy ";
        }

        if ((action & DnDConstants.ACTION_MOVE) != 0) {
            actions += "Move ";
        }

        if ((action & DnDConstants.ACTION_LINK) != 0) {
            actions += "Link";
        }

        return actions;
    }
}

