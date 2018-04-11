package com.polimi.mobileuse.model.phoneReport;

abstract class LogItemAbstract extends DateConverter{

    protected int id;
    protected int number;
    protected int type;
    protected int smartphoneSession;

    private enum TypeEnum {
        IN,
        OUT
    }
    protected TypeEnum typeEnum;

    LogItemAbstract(int id, long date, int type, int number, int smartphoneSession){
        this.id = id;
        this.date = fromMillisToCalendar(date);
        //this.time = fromStringToDate(time);

        this.type = type;
        this.typeEnum = detectType(type);
        this.number = number;
        this.smartphoneSession = smartphoneSession;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getSmartphoneSession() {
        return smartphoneSession;
    }

    public void setSmartphoneSession(int smartphoneSession) {
        this.smartphoneSession = smartphoneSession;
    }

    public String getTypeEnum() {
        return typeEnum.name();
    }

    public void setTypeEnum(TypeEnum typeEnum) {
        this.typeEnum = typeEnum;
    }

    private TypeEnum detectType(int typeIndex){
        if (typeIndex == 1){
            return TypeEnum.IN;
        }
        else if(typeIndex == 2) {
            return TypeEnum.OUT;
        }
        else{
            return null;
        }
    }

    /*

    public String getDateString() {

        return dateFormat.format(date.getTime());
    }

    public String getTime() {
        return timeFormat.format(this.date.getTime());
    }

    public void setTimeString(String time) {
        this.time = fromStringToDate(time);
    }


*/
}
