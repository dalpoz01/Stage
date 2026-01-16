package model;

/**
 * Model che rappresenta la configurazione per la generazione del report
 */
public class ReportConfig {
    private String jsonSource;
    private String birtFilePath;
    private String outputFormat;
    private String jsonSourceType;

    public ReportConfig() {
        this.outputFormat = "PDF";
        this.jsonSourceType = "API";
    }

    public String getJsonSource() {
        return jsonSource;
    }

    public void setJsonSource(String jsonSource) {
        this.jsonSource = jsonSource;
    }

    public String getBirtFilePath() {
        return birtFilePath;
    }

    public void setBirtFilePath(String birtFilePath) {
        this.birtFilePath = birtFilePath;
    }

    public String getOutputFormat() {
        return outputFormat;
    }

    public void setOutputFormat(String outputFormat) {
        this.outputFormat = outputFormat;
    }

    public String getJsonSourceType() {
        return jsonSourceType;
    }

    public void setJsonSourceType(String jsonSourceType) {
        this.jsonSourceType = jsonSourceType;
    }

    public boolean isValidConfig() {
        return jsonSource != null && !jsonSource.isBlank() 
            && birtFilePath != null && !birtFilePath.isBlank();
    }
}
