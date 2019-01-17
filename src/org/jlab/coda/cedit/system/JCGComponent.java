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

package org.jlab.coda.cedit.system;


import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.awt.image.BufferedImage;
import java.util.*;

@XmlRootElement(namespace = "component")
public class JCGComponent {
    private double                                 x;
    private double                                 y;
    private double                                 gridX;
    private double                                 gridY;
    private double                                 w;
    private double                                 h;
    private double                                 px;
    private double                                 py;
    private int                                    id;
    private String                                 name                 = "";
    private String                                 type                 = "";
    private String                                 subType              = "Et";
    private String                                 rol1                 = "undefined";
    private String                                 rol1UsrString        = "undefined";
    private String                                 rol2                 = "";
    private String                                 rol2UsrString        = "";
    private String                                 description          = "undefined";
    private String                                 userConfig           = "undefined";
    private int                                    priority             = -1;
    private boolean                                codaComponent        = true;
    private boolean                                codaVersion2         = false;
    private boolean                                preDefined           = false;
    private String                                 nodeList             = "undefined";
    private String                                 command              = "undefined";
    private boolean                                isMaster             = false;

    private JCGModule module;

    @XmlElementWrapper(name = "links")
    @XmlElement(name="link")
    private Set<JCGLink> links
            = Collections.synchronizedSet(new HashSet<JCGLink>());
    @XmlElementWrapper(name = "transports")
    @XmlElement(name="transport")
    private Set<JCGTransport> transports
            = Collections.synchronizedSet(new HashSet<JCGTransport>());
    @XmlElementWrapper(name = "processes")
    @XmlElement(name="process")
    private Set<JCGProcess> processes
            = Collections.synchronizedSet(new HashSet<JCGProcess>());
    private BufferedImage                          image;

    public JCGComponent(){
        module = new JCGModule();
        module.setName("Main");
    }

    public JCGComponent(String name){
        module = new JCGModule();
        module.setName("Main");
        setName(name);
    }


    public JCGComponent(double x, double y, double gridX, double gridY, double w, double h, double px, double py,
                        int id, String name, String type, String subType, String rol1, String rol1UsrString,
                        String rol2, String rol2UsrString, String description, String userConfig, int priority,
                        boolean codaComponent, boolean codaVersion2, boolean preDefined, String nodeList,
                        String command, boolean isMaster, JCGModule module, Set<JCGLink> links,
                        Set<JCGTransport> transports, Set<JCGProcess> processes, BufferedImage image) {
        this.x = x;
        this.y = y;
        this.gridX = gridX;
        this.gridY = gridY;
        this.w = w;
        this.h = h;
        this.px = px;
        this.py = py;
        this.id = id;
        this.name = name;
        this.type = type;
        this.subType = subType;
        this.rol1 = rol1;
        this.rol1UsrString = rol1UsrString;
        this.rol2 = rol2;
        this.rol2UsrString = rol2UsrString;
        this.description = description;
        this.userConfig = userConfig;
        this.priority = priority;
        this.codaComponent = codaComponent;
        this.codaVersion2 = codaVersion2;
        this.preDefined = preDefined;
        this.nodeList = nodeList;
        this.command = command;
        this.isMaster = isMaster;
        this.module = module;
        this.image = image;

        for(JCGLink l:links){
            this.links.add(JCTools.deepCpLink(l));
        }

        for(JCGTransport t:transports){
            this.transports.add(JCTools.deepCpTransport(t));
        }

        for(JCGProcess p:processes){
            this.processes.add(JCTools.deepCpProcess(p));
        }
    }

    public boolean isCodaComponent() {
        return codaComponent;
    }

    public void setCodaComponent(boolean codaComponent) {
        this.codaComponent = codaComponent;
    }

    public boolean isCodaVersion2() {
        return codaVersion2;
    }

    public void setCodaVersion2(boolean codaVersion2) {
        this.codaVersion2 = codaVersion2;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSubType() {
        return subType;
    }

    public void setSubType(String subType) {
        this.subType = subType;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getW() {
        return w;
    }

    public void setW(double w) {
        this.w = w;
    }

    public double getH() {
        return h;
    }

    public void setH(double h) {
        this.h = h;
    }


    public double getPx() {
        return px;
    }

    public void setPx(double px) {
        this.px = px;
    }

    public double getPy() {
        return py;
    }

    public void setPy(double py) {
        this.py = py;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
        module.setId(id);
    }

    public Set<JCGProcess> getPrcesses() {
        return processes;
    }

    public void setPrcesses(Set<JCGProcess> processes) {
        this.processes = processes;
    }

    public void addPrcess(JCGProcess p){
        removePrcess(p);
        processes.add(p);
    }

    public void removePrcess(JCGProcess pn){
        JCGProcess p = null;
        for(JCGProcess tmp:processes){
            if(tmp.getName().equals(pn.getName())){
                p = tmp;
                break;
            }
        }
        if(p!=null)
            processes.remove(p);
    }

    public void removeAllProcesses(){
        processes.clear();
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRol1() {
        return rol1;
    }

    public void setRol1(String rol1) {
        this.rol1 = rol1;
    }

    public String getRol1UsrString() {
        return rol1UsrString;
    }

    public void setRol1UsrString(String rol1UsrString) {
        this.rol1UsrString = rol1UsrString;
    }

    public String getRol2() {
        return rol2;
    }

    public void setRol2(String rol2) {
        this.rol2 = rol2;
    }

    public String getRol2UsrString() {
        return rol2UsrString;
    }

    public void setRol2UsrString(String rol2UsrString) {
        this.rol2UsrString = rol2UsrString;
    }

    public Set<JCGTransport> getTrnsports() {
        return transports;
    }

    public void setTrnsports(Set<JCGTransport> transports) {
        this.transports = transports;
    }

    public void addTrnsport(JCGTransport transport) {
        removeTrnsport(transport);
        transports.add(transport);
    }


    public void removeTrnsports(){
        if(transports!=null && !transports.isEmpty())
            transports.clear();
    }

    public void removeTrnsport(JCGTransport tn){
        JCGTransport t = null;
        for(JCGTransport tmp:transports){
            if(tmp.getName().equals(tn.getName())){
                t = tmp;
                break;
            }
        }
        if(t!=null)
            transports.remove(t);
    }

    public Set<JCGLink> getLnks() {
        return links;
    }

    public void setLnks(Set<JCGLink> links) {
        this.links = links;
    }

    public void addLnk(JCGLink link) {
        removeLnk(link);
        links.add(link);
//
//        boolean found = false;
//        for(JCGLink l: links){
//            if(l.getName().equals(link.getName())){
//                found = true;
//                break;
//            }
//        }
//        if(!found)links.add(link);
    }


    public void removeLnks() {
        if(links!=null && !links.isEmpty()){
            links.clear();
        }
    }

    public void removeLnk(JCGLink ln){
        JCGLink l = null;
        for(JCGLink tmp:links){
            if(tmp.getName().equals(ln.getName())){
                l = tmp;
                break;
            }
        }
        if(l!=null)
            links.remove(l);
    }

    public JCGModule getModule() {
        return module;
    }

    public void setModule(JCGModule module) {
        this.module = module;
    }

    public String getUserConfig() {
        return userConfig;
    }

    public void setUserConfig(String userConfig) {
        this.userConfig = userConfig;
    }

    public StringBuffer createCode(){
        StringBuffer sb = new StringBuffer();
        if(!rol1.trim().equals("")) {

            sb.append("{").append(rol1);

            sb.append(" ");

            if(!rol1UsrString.trim().equals("")) sb.append(rol1UsrString);

            sb.append("} ");
        }
        if(!rol2.trim().equals("")) {

            sb.append("{").append(rol2);

            sb.append(" ");

            if(!rol2UsrString.trim().equals("")) sb.append(rol2UsrString);

            sb.append("}");
        }

        return  sb;
    }

    public void setCode(String code){
        StringTokenizer k1,k2,k3;
        k1 = new StringTokenizer(code,"{");

        if(k1.hasMoreTokens()){
            k2 = new StringTokenizer(k1.nextToken(),"}");
            if(k2.hasMoreTokens()){
                k3 = new StringTokenizer(k2.nextToken());
                if(k3.hasMoreTokens()) rol1 = k3.nextToken();
                if(k3.hasMoreTokens()) rol1UsrString = k3.nextToken();
            }
        }
        if(k1.hasMoreTokens()){
            k2 = new StringTokenizer(k1.nextToken(),"}");
            if(k2.hasMoreTokens()){
                k3 = new StringTokenizer(k2.nextToken());
                if(k3.hasMoreTokens()) rol2 = k3.nextToken();
                if(k3.hasMoreTokens()) rol2UsrString = k3.nextToken();
            }
        }

    }

    public double getGridX() {
        return gridX;
    }

    public void setGridX(double gridX) {
        this.gridX = gridX;
    }

    public double getGridY() {
        return gridY;
    }

    public void setGridY(double gridY) {
        this.gridY = gridY;
    }

    public boolean isPreDefined() {
        return preDefined;
    }

    public void setPreDefined(boolean preDefined) {
        this.preDefined = preDefined;
    }

    @XmlTransient
    public BufferedImage getImage() {
        return image;
    }
    public void setImage(BufferedImage image) {
        this.image = image;
    }


    public String getNodeList() {
        return nodeList;
    }

    public void setNodeList(String nodeList) {
        this.nodeList = nodeList;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public boolean isMaster() {
        return isMaster;
    }

    public void setMaster(boolean master) {
        isMaster = master;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JCGComponent that = (JCGComponent) o;
        if (codaComponent != that.isCodaComponent()) return false;
        if (codaVersion2 != that.isCodaVersion2()) return false;
        if (Double.compare(that.getH(), h) != 0) return false;
        if (id != that.id) return false;
        if (isMaster != that.isMaster) return false;
//        if (preDefined != that.isPreDefined()) return false;
        if (priority != that.getPriority()) return false;
        if (Double.compare(that.getPx(), px) != 0) return false;
        if (Double.compare(that.getPy(), py) != 0) return false;
        if (Double.compare(that.getW(), w) != 0) return false;
        if (Double.compare(that.getX(), x) != 0) return false;
        if (Double.compare(that.getY(), y) != 0) return false;
        if (!name.equals(that.getName())) return false;
        if (!rol1.equals(that.getRol1())) return false;
        if (!rol1UsrString.equals(that.getRol1UsrString())) return false;
        if (!rol2.equals(that.getRol2())) return false;
        if (!rol2UsrString.equals(that.getRol2UsrString())) return false;
        if (!type.equals(that.getType())) return false;
        if (!module.equals(that.getModule())) return false;

        if (!userConfig.equals(that.getUserConfig())) return false;
        if (!description.equals(that.getDescription())) return false;


        if(links.size()!=that.getLnks().size()) return false;
        else {
            for(JCGLink t_this:links){
                boolean f = false;
                for(JCGLink t_that:that.getLnks()){
                    if(t_this.equals(t_that)){
                        f = true;
                        break;
                    }
                }
                if(!f) return false;
            }
        }

        if(processes.size()!=that.getPrcesses().size()) return false;
        else {
            for(JCGProcess t_this:processes){
                boolean f = false;
                for(JCGProcess t_that:that.getPrcesses()){
                    if(t_this.equals(t_that)){
                        f = true;
                        break;
                    }
                }
                if(!f) return false;
            }
        }

        if(transports.size()!=that.getTrnsports().size()) return false;
        else {
             for(JCGTransport t_this:transports){
                 boolean f = false;
                 for(JCGTransport t_that:that.getTrnsports()){
                     if(t_this.equals(t_that)){
                         f = true;
                         break;
                     }
                 }
                 if(!f) return false;
             }
        }
        return true;
    }

    @Override
    public String toString() {
        return "JCGComponent{" +
                "x=" + x +
                ",\n y=" + y +
                ",\n gridX=" + gridX +
                ",\n gridY=" + gridY +
                ",\n w=" + w +
                ",\n h=" + h +
                ",\n px=" + px +
                ",\n py=" + py +
                ",\n id=" + id +
                ",\n name='" + name + '\'' +
                ",\n type='" + type + '\'' +
                ",\n subType='" + subType + '\'' +
                ",\n rol1='" + rol1 + '\'' +
                ",\n rol1UsrString='" + rol1UsrString + '\'' +
                ",\n rol2='" + rol2 + '\'' +
                ",\n rol2UsrString='" + rol2UsrString + '\'' +
                ",\n description='" + description + '\'' +
                ",\n userConfig='" + userConfig + '\'' +
                ",\n codaComponent=" + codaComponent +
                ",\n codaVersion2=" + codaVersion2 +
                ",\n preDefined=" + preDefined +
                ",\n nodeList='" + nodeList + '\'' +
                ",\n command='" + command + '\'' +
                ",\n isMaster=" + isMaster +
                ",\n module=" + module +
                ",\n links=" + links +
                ",\n transports=" + transports +
                ",\n processes=" + processes +
                ",\n image=" + image +
                '}';
    }
}
