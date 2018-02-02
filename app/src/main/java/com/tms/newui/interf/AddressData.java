package com.tms.newui.interf;

public class AddressData {
        public String mainText;
        public String secondText;

        AddressData(String main, String second) {
            mainText = main;
            secondText = second;
        }

        public String getMainText() {
            return mainText;
        }

        public void setMainText(String mainText) {
            this.mainText = mainText;
        }
    }