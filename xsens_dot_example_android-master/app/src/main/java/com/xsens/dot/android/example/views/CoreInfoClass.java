package com.xsens.dot.android.example.views;

import java.util.Date;

public class CoreInfoClass {
    private int id;
    private String North, East, Zdir, Latitude, Longitude, Site, Core, Subcore, Note,
            Project, SunReading, Altitude, Dip_Dir, Dip, Strat, Date, Time;

    public CoreInfoClass(int id, String north, String east, String zdir, String latitude, String longitude, String site,
                         String core, String subcore, String note, String project, String sunReading, String altitude,
                         String dip_Dir, String dip, String strat, String date, String time
                         ) {
        this.id = id;
        North = north;
        East = east;
        Zdir = zdir;
        Latitude = latitude;
        Longitude = longitude;
        Site = site;
        Core = core;
        Subcore = subcore;
        Note = note;
        Project = project;
        SunReading = sunReading;
        Altitude = altitude;
        Dip_Dir = dip_Dir;
        Dip = dip;
        Strat = strat;
        Date = date;
        Time = time;
    }



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNorth() {
        return North;
    }

    public void setNorth(String north) {
        North = north;
    }

    public String getEast() {
        return East;
    }

    public void setEast(String east) {
        East = east;
    }

    public String getZdir() {
        return Zdir;
    }

    public void setZdir(String zdir) {
        Zdir = zdir;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    public String getSite() {
        return Site;
    }

    public void setSite(String site) {
        Site = site;
    }

    public String getCore() {
        return Core;
    }

    public void setCore(String core) {
        Core = core;
    }

    public String getSubcore() {
        return Subcore;
    }

    public void setSubcore(String subcore) {
        Subcore = subcore;
    }

    public String getNote() {
        return Note;
    }

    public void setNote(String note) {
        Note = note;
    }

    public String getProject() {
        return Project;
    }

    public void setProject(String project) {
        Project = project;
    }

    public String getSunReading() {
        return SunReading;
    }

    public void setSunReading(String sunReading) {
        SunReading = sunReading;
    }

    public String getAltitude() {
        return Altitude;
    }

    public void setAltitude(String altitude) {
        Altitude = altitude;
    }

    public String getDip_Dir() {
        return Dip_Dir;
    }

    public void setDip_Dir(String dip_Dir) {
        Dip_Dir = dip_Dir;
    }

    public String getDip() {
        return Dip;
    }

    public void setDip(String dip) {
        Dip = dip;
    }

    public String getStrat() {
        return Strat;
    }

    public void setStrat(String strat) {
        Strat = strat;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    @Override
    public String toString() {
        return String.valueOf(id);
                /*+
                " Core=" + Core + '|' +
                " North=" + North + '|' +
                " East=" + East + '|' +
                " Sun=" + SunReading + '|' +
                " Zdir=" + Zdir + '|'
                */

    }
}
