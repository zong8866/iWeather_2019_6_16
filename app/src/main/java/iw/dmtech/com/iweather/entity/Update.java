/**
  * Copyright 2019 bejson.com 
  */
package iw.dmtech.com.iweather.entity;
import java.util.Date;

/**
 * Auto-generated: 2019-06-15 20:21:18
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class Update {

    private Date loc;
    private Date utc;
    public void setLoc(Date loc) {
         this.loc = loc;
     }
     public CharSequence getLoc() {
         return loc;
     }

    public void setUtc(Date utc) {
         this.utc = utc;
     }
     public Date getUtc() {
         return utc;
     }

}