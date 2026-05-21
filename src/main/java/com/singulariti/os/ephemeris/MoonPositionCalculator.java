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
package com.singulariti.os.ephemeris;

import com.singulariti.os.ephemeris.domain.MoonPosition;
import com.singulariti.os.ephemeris.domain.Observatory;
import com.singulariti.os.ephemeris.domain.RiseSetTimes;
import com.singulariti.os.ephemeris.utils.DateTimeUtils;
import com.singulariti.os.ephemeris.utils.FormatUtils;
import com.singulariti.os.ephemeris.utils.MathUtils;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author John
 */
public class MoonPositionCalculator {

    private static final double EARTH_EQUATORIAL_RADIUS_KM = 6378.14;
    private static final double HORIZON_REFRACTION_DEGREES = 34.0 / 60.0;
    private static final int RISE_SET_REFINEMENT_STEPS = 20;

    // Meeus first edition table 45.A Longitude and distance of the moon
    public static final int[] T45AD = new int[]{
        0, 2, 2, 0, 0, 0, 2, 2, 2, 2,
        0, 1, 0, 2, 0, 0, 4, 0, 4, 2,
        2, 1, 1, 2, 2, 4, 2, 0, 2, 2,
        1, 2, 0, 0, 2, 2, 2, 4, 0, 3,
        2, 4, 0, 2, 2, 2, 4, 0, 4, 1,
        2, 0, 1, 3, 4, 2, 0, 1, 2, 2
    };

    public static final int[] T45AM = new int[]{
        0, 0, 0, 0, 1, 0, 0, -1, 0, -1,
        1, 0, 1, 0, 0, 0, 0, 0, 0, 1,
        1, 0, 1, -1, 0, 0, 0, 1, 0, -1,
        0, -2, 1, 2, -2, 0, 0, -1, 0, 0,
        1, -1, 2, 2, 1, -1, 0, 0, -1, 0,
        1, 0, 1, 0, 0, -1, 2, 1, 0, 0
    };

    public static final int[] T45AMP = new int[]{
        1, -1, 0, 2, 0, 0, -2, -1, 1, 0,
        -1, 0, 1, 0, 1, 1, -1, 3, -2, -1,
        0, -1, 0, 1, 2, 0, -3, -2, -1, -2,
        1, 0, 2, 0, -1, 1, 0, -1, 2, -1,
        1, -2, -1, -1, -2, 0, 1, 4, 0, -2,
        0, 2, 1, -2, -3, 2, 1, -1, 3, -1
    };

    public static final int[] T45AF = new int[]{
        0, 0, 0, 0, 0, 2, 0, 0, 0, 0,
        0, 0, 0, -2, 2, -2, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 2, 0,
        0, 0, 0, 0, 0, -2, 2, 0, 2, 0,
        0, 0, 0, 0, 0, -2, 0, 0, 0, 0,
        -2, -2, 0, 0, 0, 0, 0, 0, 0, -2
    };

    public static final int[] T45AL = new int[]{
        6288774, 1274027, 658314, 213618, -185116,
        -114332, 58793, 57066, 53322, 45758,
        -40923, -34720, -30383, 15327, -12528,
        10980, 10675, 10034, 8548, -7888,
        -6766, -5163, 4987, 4036, 3994,
        3861, 3665, -2689, -2602, 2390,
        -2348, 2236, -2120, -2069, 2048,
        -1773, -1595, 1215, -1110, -892,
        -810, 759, -713, -700, 691,
        596, 549, 537, 520, -487,
        -399, -381, 351, -340, 330,
        327, -323, 299, 294, 0
    };

    public static final int[] T45AR = new int[]{
        -20905355, -3699111, -2955968, -569925, 48888,
        -3149, 246158, -152138, -170733, -204586,
        -129620, 108743, 104755, 10321, 0,
        79661, -34782, -23210, -21636, 24208,
        30824, -8379, -16675, -12831, -10445,
        -11650, 14403, -7003, 0, 10056,
        6322, -9884, 5751, 0, -4950,
        4130, 0, -3958, 0, 3258,
        2616, -1897, -2117, 2354, 0,
        0, -1423, -1117, -1571, -1739,
        0, -4421, 0, 0, 0,
        0, 1165, 0, 0, 8752
    };

    // Meeus table 45B latitude of the moon
    public static final int[] T45BD = new int[]{0, 0, 0, 2, 2, 2, 2, 0, 2, 0,
        2, 2, 2, 2, 2, 2, 2, 0, 4, 0,
        0, 0, 1, 0, 0, 0, 1, 0, 4, 4,
        0, 4, 2, 2, 2, 2, 0, 2, 2, 2,
        2, 4, 2, 2, 0, 2, 1, 1, 0, 2,
        1, 2, 0, 4, 4, 1, 4, 1, 4, 2};

    public static final int[] T45BM = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        -1, 0, 0, 1, -1, -1, -1, 1, 0, 1,
        0, 1, 0, 1, 1, 1, 0, 0, 0, 0,
        0, 0, 0, 0, -1, 0, 0, 0, 0, 1,
        1, 0, -1, -2, 0, 1, 1, 1, 1, 1,
        0, -1, 1, 0, -1, 0, 0, 0, -1, -2};

    public static final int[] T45BMP = new int[]{0, 1, 1, 0, -1, -1, 0, 2, 1, 2,
        0, -2, 1, 0, -1, 0, -1, -1, -1, 0,
        0, -1, 0, 1, 1, 0, 0, 3, 0, -1,
        1, -2, 0, 2, 1, -2, 3, 2, -3, -1,
        0, 0, 1, 0, 1, 1, 0, 0, -2, -1,
        1, -2, 2, -2, -1, 1, 1, -1, 0, 0};

    public static final int[] T45BF = new int[]{1, 1, -1, -1, 1, -1, 1, 1, -1, -1,
        -1, -1, 1, -1, 1, 1, -1, -1, -1, 1,
        3, 1, 1, 1, -1, -1, -1, 1, -1, 1,
        -3, 1, -3, -1, -1, 1, -1, 1, -1, 1,
        1, 1, 1, -1, 3, -1, -1, 1, -1, -1,
        1, -1, 1, -1, -1, -1, -1, -1, -1, 1};

    public static final int[] T45BL = new int[]{5128122, 280602, 277693, 173237, 55413,
        46271, 32573, 17198, 9266, 8822,
        8216, 4324, 4200, -3359, 2463,
        2211, 2065, -1870, 1828, -1794,
        -1749, -1565, -1491, -1475, -1410,
        -1344, -1335, 1107, 1021, 833,
        777, 671, 607, 596, 491,
        -451, 439, 422, 421, -366,
        -351, 331, 315, 302, -283,
        -229, 223, 223, -220, -220,
        -185, 181, -177, 176, 166,
        -164, 132, -119, 115, 107};

    // MoonPos calculates the Moon position, based on Meeus chapter 45
    // and the illuminated percentage from Meeus equations 46.4 and 46.1
    //// returns an array containing rise and set times or one of the
    // following codes.
    // -1 rise or set event not found and moon was down at 00:00
    // -2 rise or set event not found and moon was up   at 00:00
    public static List<Double> moonpos(Observatory obs) {
        // julian date
        double jdobs = DateTimeUtils.jd(obs);
        double T = (jdobs - 2451545.0) / 36525;
        double T2 = T * T;
        double T3 = T2 * T;
        double T4 = T3 * T;
        // Moons mean longitude L'
        double LP = 218.3164477 + 481267.88123421 * T - 0.0015786 * T2 + T3 / 538841.0 - T4 / 65194000.0;
        // Moons mean elongation Meeus first edition
        // var D=297.8502042+445267.1115168*T-0.0016300*T2+T3/545868.0-T4/113065000.0;
        // Moons mean elongation Meeus second edition
        double D = 297.8501921 + 445267.1114034 * T - 0.0018819 * T2 + T3 / 545868.0 - T4 / 113065000.0;

        // Moons mean anomaly M' Meeus first edition
        // var MP=134.9634114+477198.8676313*T+0.0089970*T2+T3/69699.0-T4/14712000.0;
        // Moons mean anomaly M' Meeus second edition
        double MP = 134.9633964 + 477198.8675055 * T + 0.0087414 * T2 + T3 / 69699.0 - T4 / 14712000.0;
        // Moons argument of latitude
        double F = 93.2720950 + 483202.0175233 * T - 0.0036539 * T2 - T3 / 3526000.0 + T4 / 863310000.0;
        // Suns mean anomaly
        double M = 357.5291092 + 35999.0502909 * T - 0.0001536 * T2 + T3 / 24490000.0;
        // Additional arguments
        double A1 = 119.75 + 131.849 * T;
        double A2 = 53.09 + 479264.290 * T;
        double A3 = 313.45 + 481266.484 * T;
        double E = 1 - 0.002516 * T - 0.0000074 * T2;
        double E2 = E * E;
        // Sums of periodic terms from table 45.A and 45.B
        double Sl = 0.0;
        double Sr = 0.0;
        for (int i = 0; i < 60; i++) {
            double Eterm = 1;
            if (Math.abs(T45AM[i]) == 1) {
                Eterm = E;
            }
            if (Math.abs(T45AM[i]) == 2) {
                Eterm = E2;
            }
            Sl += T45AL[i] * Eterm * MathUtils.sind(MathUtils.rev(T45AD[i] * D + T45AM[i] * M + T45AMP[i] * MP + T45AF[i] * F));
            Sr += T45AR[i] * Eterm * MathUtils.cosd(MathUtils.rev(T45AD[i] * D + T45AM[i] * M + T45AMP[i] * MP + T45AF[i] * F));
        }

        double Sb = 0.0;
        for (int i = 0; i < 60; i++) {
            double Eterm = 1;
            if (Math.abs(T45BM[i]) == 1) {
                Eterm = E;
            }
            if (Math.abs(T45BM[i]) == 2) {
                Eterm = E2;
            }
            Sb += T45BL[i] * Eterm * MathUtils.sind(MathUtils.rev(T45BD[i] * D + T45BM[i] * M + T45BMP[i] * MP + T45BF[i] * F));
        }

        // Additional additive terms
        Sl = Sl + 3958 * MathUtils.sind(MathUtils.rev(A1)) + 1962 * MathUtils.sind(MathUtils.rev(LP - F)) + 318 * MathUtils.sind(MathUtils.rev(A2));
        Sb = Sb - 2235 * MathUtils.sind(MathUtils.rev(LP)) + 382 * MathUtils.sind(MathUtils.rev(A3)) + 175 * MathUtils.sind(MathUtils.rev(A1 - F))
                + 175 * MathUtils.sind(MathUtils.rev(A1 + F)) + 127 * MathUtils.sind(MathUtils.rev(LP - MP)) - 115 * MathUtils.sind(MathUtils.rev(LP + MP));

        // geocentric longitude, latitude and distance
        double mglong = MathUtils.rev(LP + Sl / 1000000.0);
        double mglat = MathUtils.rev(Sb / 1000000.0);
        if (mglat > 180.0) {
            mglat = mglat - 360;
        }

        double mr = Math.round(385000.56 + Sr / 1000.0);
        // Obliquity of Ecliptic
        double obl = 23.4393 - 3.563E-7 * (jdobs - 2451543.5);
        // RA and dec
        double ra = MathUtils.rev(MathUtils.atan2d(MathUtils.sind(mglong) * MathUtils.cosd(obl) - MathUtils.tand(mglat) * MathUtils.sind(obl),
                MathUtils.cosd(mglong))) / 15.0;
        double dec = MathUtils.rev(MathUtils.asind(MathUtils.sind(mglat) * MathUtils.cosd(obl) + MathUtils.cosd(mglat) * MathUtils.sind(obl) * MathUtils.sind(mglong)));
        if (dec > 180.0) {
            dec = dec - 360;
        }
        // phase angle
        double pa = 180.0 - D - 6.289 * MathUtils.sind(MP) + 2.1 * MathUtils.sind(M) - 1.274 * MathUtils.sind(2 * D - MP)
                - 0.658 * MathUtils.sind(2 * D) - 0.214 * MathUtils.sind(2 * MP) - 0.11 * MathUtils.sind(D);
        // Altitude and azimuth
        double[] altaz = MathUtils.radtoaa(ra, dec, obs);
        return Arrays.asList(ra, dec, mr, altaz[0], altaz[1], MathUtils.rev(pa));
    }

    public RiseSetTimes moonrise(Observatory obs) {
        Observatory obsReset = obs.copy();
        ZonedDateTime dayStart = obs.getCurrentTime().withHour(0).withMinute(0).withSecond(0).withNano(0);

        double previousAltitude = adjustedMoonAltitude(obsReset, dayStart);
        RiseSetTimes times = previousAltitude >= 0.0 ? new RiseSetTimes("-2", "-2") : new RiseSetTimes("-1", "-1");
        boolean foundRise = false;
        boolean foundSet = false;

        for (int hour = 1; hour <= 24; hour++) {
            double currentAltitude = adjustedMoonAltitude(obsReset, dayStart.plusHours(hour));

            if (!foundRise && previousAltitude <= 0.0 && currentAltitude >= 0.0) {
                double value = refineMoonEvent(obsReset, dayStart, hour - 1.0, hour, previousAltitude);
                times.setRise(String.valueOf(value));
                foundRise = true;
            }

            if (!foundSet && previousAltitude >= 0.0 && currentAltitude <= 0.0) {
                double value = refineMoonEvent(obsReset, dayStart, hour - 1.0, hour, previousAltitude);
                times.setSet(String.valueOf(value));
                foundSet = true;
            }

            previousAltitude = currentAltitude;
            if (foundRise && foundSet) {
                break;
            }
        }

        return times;
    }

    private double refineMoonEvent(Observatory obs, ZonedDateTime dayStart, double startHour, double endHour, double startAltitude) {
        double lowHour = startHour;
        double highHour = endHour;
        double lowAltitude = startAltitude;

        for (int i = 0; i < RISE_SET_REFINEMENT_STEPS; i++) {
            double midHour = (lowHour + highHour) / 2.0;
            double midAltitude = adjustedMoonAltitude(obs, timeAtHour(dayStart, midHour));
            if ((lowAltitude <= 0.0 && midAltitude >= 0.0) || (lowAltitude >= 0.0 && midAltitude <= 0.0)) {
                highHour = midHour;
            } else {
                lowHour = midHour;
                lowAltitude = midAltitude;
            }
        }

        return (lowHour + highHour) / 2.0;
    }

    private double adjustedMoonAltitude(Observatory obs, ZonedDateTime time) {
        obs.setCurrentTime(time);
        List<Double> moontab = moonpos(obs);
        return moontab.get(3) - moonRiseSetAltitude(moontab.get(2));
    }

    private double moonRiseSetAltitude(double distanceKm) {
        double horizontalParallax = MathUtils.asind(EARTH_EQUATORIAL_RADIUS_KM / distanceKm);
        return 0.7275 * horizontalParallax - HORIZON_REFRACTION_DEGREES;
    }

    private ZonedDateTime timeAtHour(ZonedDateTime dayStart, double hour) {
        long seconds = Math.round(hour * 3600.0);
        return dayStart.plusSeconds(seconds);
    }

    public List<MoonPosition> getEphemeris(Observatory obs, ZonedDateTime startDate, ZonedDateTime endDate, int intervalMinutes) {
        List<MoonPosition> ephemerides = new ArrayList<>();
        ZonedDateTime currentTime = startDate;
        while (currentTime.isBefore(endDate)) {
            obs.setCurrentTime(currentTime);
            MoonPosition eph = getPosition(obs);
            ephemerides.add(eph);

            currentTime = currentTime.plusMinutes(intervalMinutes);
        }

        obs.setCurrentTime(endDate);
        MoonPosition eph = getPosition(obs);
        ephemerides.add(eph);

        return ephemerides;
    }

    public MoonPosition getPosition(Observatory obs) {
        String siteName = FormatUtils.sitename(obs);

        List<Double> moontab = moonpos(obs);
        String ra = FormatUtils.hmdstring(moontab.get(0));
        String dec = FormatUtils.anglestring(moontab.get(1), false);
        String altitude = FormatUtils.anglestring(moontab.get(3), false);
        String azimuth = FormatUtils.anglestring(moontab.get(4), true);
        String dist = String.valueOf(moontab.get(2));

        String pa = String.valueOf(Math.round(moontab.get(5)));
        String ip = String.valueOf(Math.round(100.0 * (1.0 + MathUtils.cosd(moontab.get(5))) / 2.0));

        RiseSetTimes riseSetTimes = moonrise(obs);

        MoonPosition eph = new MoonPosition();
        eph.setSiteName(siteName);
        eph.setDate(obs.getCurrentTime());
        eph.setRa(ra);
        eph.setDec(dec);
        eph.setAltitude(altitude);
        eph.setAzimuth(azimuth);
        eph.setDistance(dist);
        eph.setPhaseAngle(pa);
        eph.setIlluminatedPercentage(ip);
        eph.setRiseTime(riseSetTimes.getRise());
        eph.setSetTime(riseSetTimes.getSet());

        return eph;
    }
}
