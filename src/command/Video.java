package command;

import java.util.Objects;

public final class Video {
    // pentru un video definesc numarul sezonului curent si nota primita
    // si numarul total de sezoane al acestuia
    private final Integer number;
    private final Double grade;
    private int seasonNumber;

    public Video(final Integer number, final Double grade) {
        this.number = number;
        this.grade = grade;
    }

    public Integer getNumber() {
        return number;
    }

    public Double getGrade() {
        return grade;
    }

    public void setSeasonNumber(final int seasonNumber) {
        this.seasonNumber = seasonNumber;
    }

    public int getSeasonNumber() {
        return seasonNumber;
    }

    @Override
    public String toString() {
        return "Video{"
                + "number=" + number
                + ", grade=" + grade
                + ", seasonNumber=" + seasonNumber
                + '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Video)) {
            return false;
        }
        Video video = (Video) o;
        return Objects.equals(number, video.number)
                && Objects.equals(grade, video.grade);
    }

    @Override
    public int hashCode() {
        return Objects.hash(number, grade);
    }

}
