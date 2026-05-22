/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.singulariti.os.ephemeris.domain;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author John
 */
public class SunPosition {

    private String siteName;
    private ZonedDateTime date;
    private String ra;
    private String dec;
    private String altitude;
    private String azimuth;
    private String earthDistance;
    private ZonedDateTime riseTime;
    private ZonedDateTime setTime;
    private ZonedDateTime civilDawnTime;
    private ZonedDateTime civilDuskTime;
    private ZonedDateTime nauticalDawnTime;
    private ZonedDateTime nauticalDuskTime;
    private ZonedDateTime astronomicalDawnTime;
    private ZonedDateTime astronomicalDuskTime;
    private RiseSetStatus riseSetStatus;
    private RiseSetStatus civilTwilightStatus;
    private RiseSetStatus nauticalTwilightStatus;
    private RiseSetStatus astronomicalTwilightStatus;
    private String dstCorrected;

    public SunPosition() {
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public String getRa() {
        return ra;
    }

    public void setRa(String ra) {
        this.ra = ra;
    }

    public String getDec() {
        return dec;
    }

    public void setDec(String dec) {
        this.dec = dec;
    }

    public String getAltitude() {
        return altitude;
    }

    public void setAltitude(String altitude) {
        this.altitude = altitude;
    }

    public String getAzimuth() {
        return azimuth;
    }

    public void setAzimuth(String azimuth) {
        this.azimuth = azimuth;
    }

    public String getEarthDistance() {
        return earthDistance;
    }

    public void setEarthDistance(String earthDistance) {
        this.earthDistance = earthDistance;
    }

    public ZonedDateTime getRiseTime() {
        return riseTime;
    }

    public void setRiseTime(ZonedDateTime riseTime) {
        this.riseTime = riseTime;
    }

    public ZonedDateTime getSetTime() {
        return setTime;
    }

    public void setSetTime(ZonedDateTime setTime) {
        this.setTime = setTime;
    }

    public ZonedDateTime getCivilDawnTime() {
        return civilDawnTime;
    }

    public void setCivilDawnTime(ZonedDateTime civilDawnTime) {
        this.civilDawnTime = civilDawnTime;
    }

    public ZonedDateTime getCivilDuskTime() {
        return civilDuskTime;
    }

    public void setCivilDuskTime(ZonedDateTime civilDuskTime) {
        this.civilDuskTime = civilDuskTime;
    }

    public ZonedDateTime getNauticalDawnTime() {
        return nauticalDawnTime;
    }

    public void setNauticalDawnTime(ZonedDateTime nauticalDawnTime) {
        this.nauticalDawnTime = nauticalDawnTime;
    }

    public ZonedDateTime getNauticalDuskTime() {
        return nauticalDuskTime;
    }

    public void setNauticalDuskTime(ZonedDateTime nauticalDuskTime) {
        this.nauticalDuskTime = nauticalDuskTime;
    }

    public ZonedDateTime getAstronomicalDawnTime() {
        return astronomicalDawnTime;
    }

    public void setAstronomicalDawnTime(ZonedDateTime astronomicalDawnTime) {
        this.astronomicalDawnTime = astronomicalDawnTime;
    }

    public ZonedDateTime getAstronomicalDuskTime() {
        return astronomicalDuskTime;
    }

    public void setAstronomicalDuskTime(ZonedDateTime astronomicalDuskTime) {
        this.astronomicalDuskTime = astronomicalDuskTime;
    }

    public RiseSetStatus getRiseSetStatus() {
        return riseSetStatus;
    }

    public void setRiseSetStatus(RiseSetStatus riseSetStatus) {
        this.riseSetStatus = riseSetStatus;
    }

    public RiseSetStatus getCivilTwilightStatus() {
        return civilTwilightStatus;
    }

    public void setCivilTwilightStatus(RiseSetStatus civilTwilightStatus) {
        this.civilTwilightStatus = civilTwilightStatus;
    }

    public RiseSetStatus getNauticalTwilightStatus() {
        return nauticalTwilightStatus;
    }

    public void setNauticalTwilightStatus(RiseSetStatus nauticalTwilightStatus) {
        this.nauticalTwilightStatus = nauticalTwilightStatus;
    }

    public RiseSetStatus getAstronomicalTwilightStatus() {
        return astronomicalTwilightStatus;
    }

    public void setAstronomicalTwilightStatus(RiseSetStatus astronomicalTwilightStatus) {
        this.astronomicalTwilightStatus = astronomicalTwilightStatus;
    }

    public String getDstCorrected() {
        return dstCorrected;
    }

    public void setDstCorrected(String dstCorrected) {
        this.dstCorrected = dstCorrected;
    }

    @Override
    public String toString() {
        String d = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss z").format(date);
        //date ra dec alt az earthdist risetime settime civildawn civildusk nautdawn nautdusk astdawn astdusk
        String str = String.format("%20s | %15s | %10s | %15s | %15s | %15s | %15s | %15s | %15s | %15s | %15s | %15s | %15s | %15s |",
                d, ra, dec, altitude, azimuth, earthDistance, formatEvent(riseTime, riseSetStatus), formatEvent(setTime, riseSetStatus), formatEvent(civilDawnTime, civilTwilightStatus), formatEvent(civilDuskTime, civilTwilightStatus), formatEvent(nauticalDawnTime, nauticalTwilightStatus), formatEvent(nauticalDuskTime, nauticalTwilightStatus), formatEvent(astronomicalDawnTime, astronomicalTwilightStatus), formatEvent(astronomicalDuskTime, astronomicalTwilightStatus));
        return str;
    }

    private String formatEvent(ZonedDateTime eventTime, RiseSetStatus status) {
        if (eventTime != null) {
            return DateTimeFormatter.ofPattern("dd-MM HH:mm z").format(eventTime);
        }
        return status == null ? "" : status.name();
    }

    public static String header() {
        String str = String.format("%20s | %15s | %10s | %15s | %15s | %15s | %15s | %15s | %15s | %15s | %15s | %15s | %15s | %15s |",
                "Date", "RA", "DEC", "Altitude", "Azimuth", "Earth Distance", "Rise", "Set", "Civil Rise", "Civil Set", "Naut Rise", "Naut Set", "Astr Rise", "Astr Set");
        return str;
    }

}
