package com.trading.depthcharts.model;

public record Player (int number, String name) {

    @Override
    public int hashCode() {
        return Integer.hashCode(number); 
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Player other)) 
            return false;
        return number == other.number;
    }

    @Override
    public String toString() {
        return "(#" + number + ", " + name + ")";
    }
    
}
