package com.myproject.systemdemo.domain;

public class Upload {
    private String exampleSelect;
    private String optionsRadios;
    private String status;
    private String[] checkedLights;
    private String[] checkedSwitches;
    private String[] checkedNumbers;
    private String[] checkedPoints;

    public String[] getCheckedNumbers() {
        return checkedNumbers;
    }

    public void setCheckedNumbers(String[] checkedNumbers) {
        this.checkedNumbers = checkedNumbers;
    }

    public String[] getCheckedPoints() {
        return checkedPoints;
    }

    public void setCheckedPoints(String[] checkedPoints) {
        this.checkedPoints = checkedPoints;
    }

    public String[] getCheckedLights() { return checkedLights; }

    public void setCheckedLights(String[] checkedLights) { this.checkedLights = checkedLights; }

    public String[] getCheckedSwitches() {
        return checkedSwitches;
    }

    public void setCheckedSwitches(String[] checkedSwitches) {
        this.checkedSwitches = checkedSwitches;
    }

    public String getExampleSelect() {
        return exampleSelect;
    }

    public void setExampleSelect(String exampleSelect) {
        this.exampleSelect = exampleSelect;
    }

    public String getOptionsRadios() {
        return optionsRadios;
    }

    public void setOptionsRadios(String optionsRadios) {
        this.optionsRadios = optionsRadios;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    @Override
    public String toString() {
        return "Upload{" +
                "exampleSelect='" + exampleSelect + '\'' +
                ", optionsRadios='" + optionsRadios + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
