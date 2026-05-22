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
import java.util.Objects;

/**
 *
 * @author John
 */
public class RiseSetTimes {

    private ZonedDateTime rise;
    private ZonedDateTime set;
    private RiseSetStatus status;

    public RiseSetTimes() {
        this(null, null, RiseSetStatus.NORMAL);
    }

    public RiseSetTimes(ZonedDateTime rise, ZonedDateTime set) {
        this(rise, set, RiseSetStatus.NORMAL);
    }

    public RiseSetTimes(ZonedDateTime rise, ZonedDateTime set, RiseSetStatus status) {
        this.rise = rise;
        this.set = set;
        this.status = status;
    }

    public ZonedDateTime getRise() {
        return rise;
    }

    public void setRise(ZonedDateTime rise) {
        this.rise = rise;
    }

    public ZonedDateTime getSet() {
        return set;
    }

    public void setSet(ZonedDateTime set) {
        this.set = set;
    }

    public RiseSetStatus getStatus() {
        return status;
    }

    public void setStatus(RiseSetStatus status) {
        this.status = status;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.rise);
        hash = 97 * hash + Objects.hashCode(this.set);
        hash = 97 * hash + Objects.hashCode(this.status);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final RiseSetTimes other = (RiseSetTimes) obj;
        if (!Objects.equals(this.rise, other.rise)) {
            return false;
        }
        if (!Objects.equals(this.set, other.set)) {
            return false;
        }
        if (this.status != other.status) {
            return false;
        }
        return true;
    }

}
