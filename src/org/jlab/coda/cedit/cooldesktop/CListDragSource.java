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

import javax.swing.*;
import java.awt.dnd.*;

public class CListDragSource implements DragSourceListener, DragGestureListener {

  DragSource source;

  DragGestureRecognizer recognizer;

  CTransferableListElement transferable;

  JList sourceList;

  public CListDragSource(JList list, int actions) {
    sourceList = list;
    source = new DragSource();
    recognizer = source.createDefaultDragGestureRecognizer(sourceList,
        actions, this);
  }

  /*
   * Drag Gesture Handler
   */
  public void dragGestureRecognized(DragGestureEvent dge) {
    Object o = sourceList.getSelectedValue();
    if ((o == null)) {
      // We can't move an empty selection
      return;
    }
    transferable = new CTransferableListElement(o);
    source.startDrag(dge, DragSource.DefaultMoveNoDrop, transferable, this);

    // If you support dropping the node anywhere, you should probably
    // start with a valid move cursor:
    //source.startDrag(dge, DragSource.DefaultMoveDrop, transferable,
    // this);
  }

  /*
   * Drag Event Handlers
   */
  public void dragEnter(DragSourceDragEvent dsde) {
  }

  public void dragExit(DragSourceEvent dse) {
  }

  public void dragOver(DragSourceDragEvent dsde) {
  }

  public void dropActionChanged(DragSourceDragEvent dsde) {
    System.out.println("Action: " + dsde.getDropAction());
    System.out.println("Target Action: " + dsde.getTargetActions());
    System.out.println("User Action: " + dsde.getUserAction());
  }

  public void dragDropEnd(DragSourceDropEvent dsde) {
    /*
     * to support move or copy, we have to check which occurred:
     */
//    System.out.println("Drop Action: " + dsde.getDropAction());
//    if (dsde.getDropSuccess()
//        && (dsde.getDropAction() == DnDConstants.ACTION_MOVE)) {
//      ((DefaultTreeModel) sourceList.getModel())
//          .removeNodeFromParent(oldNode);
//    }

    /*
     * to support move only... if (dsde.getDropSuccess()) {
     * ((DefaultTreeModel)sourceList.getModel()).removeNodeFromParent(oldNode); }
     */
  }
}

