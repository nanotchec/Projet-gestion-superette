package data;

import java.util.HashMap;

public interface IData {
    void getStruct();
    String getValues();
    HashMap<String, fieldType> getMap();
    boolean check(HashMap<String, fieldType> tableStruct);
    
    
    HashMap<String, Object> getFieldValues();
}