package com.bcilab.tremorapp.Data;

import java.util.ArrayList;

public class BaseData {
    private float base_x ;
    private float base_y ;

    public BaseData(float base_x, float base_y) {
        this.base_x = base_x;
        this.base_y = base_y;
    }

    public float getBase_x() {
        return base_x;
    }

    public void setBase_x(float base_x) {
        this.base_x = base_x;
    }

    public float getBase_y() {
        return base_y;
    }

    public void setBase_y(float base_y) {
        this.base_y = base_y;
    }

}
