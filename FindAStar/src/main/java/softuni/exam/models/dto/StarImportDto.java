package softuni.exam.models.dto;

import com.google.gson.annotations.Expose;
import jdk.jfr.Name;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

public class StarImportDto {
    @Expose
    @Size(min = 6)
    @NotNull
    private String description;
    @Expose
    @Positive
    @NotNull
    private Double lightYears;
    @Expose
    @Size(min = 2, max = 30)
    @NotNull
    private String name;
    @Expose
    @NotNull
    private String starType;
    @Expose
    @NotNull
    private Long constellation;

    public StarImportDto() {
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getLightYears() {
        return lightYears;
    }

    public void setLightYears(Double lightYears) {
        this.lightYears = lightYears;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStarType() {
        return starType;
    }

    public void setStarType(String starType) {
        this.starType = starType;
    }

    public Long getConstellation() {
        return constellation;
    }

    public void setConstellation(Long constellation) {
        this.constellation = constellation;
    }
}
