package com.federicoberon.alarme.api;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name = "item", strict = false)
public class HoroscopeTwo {
        @Element(name = "title")
        public String title;

        @Element(name = "link")
        public String link;

        @Element(name = "description")
        public String description;
}