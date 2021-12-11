public class SkierRecord {
  int resortID;
  String seasonID;
  String dayID;
  int skierID;


  public SkierRecord(int resortID, String seasonID, String dayID, int skierID) {
    this.resortID = resortID;
    this.seasonID = seasonID;
    this.dayID = dayID;
    this.skierID = skierID;
  }

//  public SkierRecord(){
//
//  }
//
//  public SkierRecord(int resortID, int skierID){
//    this.resortID = resortID;
//    this.skierID = skierID;
//  }

  public int getResortID() {
    return resortID;
  }

  public String getSeasonID() {
    return seasonID;
  }

  public String getDayID() {
    return dayID;
  }

  public int getSkierID() {
    return skierID;
  }

  public void setResortID(int resortID) {
    this.resortID = resortID;
  }

  public void setSeasonID(String seasonID) {
    this.seasonID = seasonID;
  }

  public void setDayID(String dayID) {
    this.dayID = dayID;
  }

  public void setSkierID(int skierID) {
    this.skierID = skierID;
  }

  @Override
  public String toString() {
    return "SkierRecord{" +
        "resortID=" + resortID +
        ", seasonID='" + seasonID + '\'' +
        ", dayID='" + dayID + '\'' +
        ", skierID=" + skierID +
        '}';
  }
}
