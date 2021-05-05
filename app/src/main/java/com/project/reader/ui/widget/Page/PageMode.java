package com.project.reader.ui.widget.Page;

public enum PageMode {
    COVER(0),SCROLL(1),SIMULATION(2), SLIDE(3), VERTICAL_COVER(4);
    private int value;
    private PageMode(int value){
        this.value=value;
    }
    public static PageMode intToEnum(int value) {    //将数值转换成枚举值
        switch (value) {
            case 0:
                return COVER;
            case 1:
                return SCROLL;
            case 2:
                return SIMULATION;
            case 3:
                return SLIDE;
            case 4:
                return VERTICAL_COVER;
            default :
                return null;
        }
    }
    public int enumToInt() { //将枚举值转换成数值
        return this.value;
    }
}

