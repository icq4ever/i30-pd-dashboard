package com.veloster.vehicle;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;

/**
 * WRC screen: aaronfang's SimHub dash "Replicate EA WRC23 in game Dashboard Collection", its 000-Default screen
 * (https://overtake.gg/downloads/replicate-ea-wrc23-in-game-dashboard-collection.67288), 1280x750 brought to 800x428 (x 0.625, y 0.571).
 * Black, a huge gear in the middle column, two shift bars each side over the top 293 px (below them the rpm and km/h
 * boxes): the outer green ones light at 7/12 of the shift target, the inner orange ones at 9/10 (the original's SimHub
 * shift lights sit just under the redline; these are wider, for everyday shifting), and at the target the gear's column
 * (down to the black band at 399) turns red.
 *
 * Type is Inter (OFL; res/raw): Medium with its tabular figures mapped to the digits (API 17 cannot turn on tnum), so the
 * numbers keep their place as they change, and Bold Italic for the RPM / KMH labels.
 */
final class WrcCluster extends ClusterKit {
    /** Inter Medium and Bold Italic, loaded by VehicleDash.fonts(); null falls back to the system face. */
    static volatile Typeface FACE,FACE_ITALIC;
    int shiftTarget=6000;   // set by PitRenderer from 설정 → RPM
    private static final float W=800,H=428,BAR_B=293,MID_B=399,GEAR=440,CAP=0.7275f,NUM=56,NUM_Y=375,LBL_Y=402;
    private static final int BG=0xff000000,FG=0xffffffff,GRN=0xff00e773,ORG=0xffffa500,RED=0xffff0000;

    private static Typeface face(){Typeface f=FACE;return f!=null?f:Typeface.DEFAULT;}
    private static Typeface italic(){Typeface f=FACE_ITALIC;return f!=null?f:Typeface.DEFAULT_BOLD;}

    @Override protected void draw(Canvas c){
        c.save();c.translate(0,52);
        fill(c,0,0,W,H,BG);
        float rpm=v(0),kmh=v(7);
        boolean ok=!Float.isNaN(rpm);
        // four steps, wide as on the 랠리 layout's rev bar: none, green at 7/12 of the shift target, orange at 9/10, red
        // at it (the default 6,000: 3,500 / 5,400 / 6,000; they follow 설정 → RPM)
        if(ok&&rpm>=shiftTarget*7f/12){fill(c,0,0,102,BAR_B,GRN);fill(c,698,0,W,BAR_B,GRN);}
        if(ok&&rpm>=shiftTarget*9f/10){fill(c,102,0,204,BAR_B,ORG);fill(c,596,0,698,BAR_B,ORG);}
        if(ok&&rpm>=shiftTarget)fill(c,204,0,596,MID_B,RED);
        // the gear fills the middle column, centred on its cap height; the DCT manual gate shows the bare number (3, not
        // M3) as on the 타코 screen, and anything wider than the 392 px column is scaled to fit
        String g=gear();if(manualGate(g))g=g.substring(1);
        float size=GEAR,gw=width(g,size,face());if(gw>372)size*=372/gw;
        text(c,g,400,MID_B/2+size*CAP/2,size,FG,Paint.Align.CENTER,face());
        fill(c,204,MID_B,596,H,BG);
        text(c,ok?String.format(java.util.Locale.US,"%04d",Math.round(rpm)):"—",107,NUM_Y,NUM,FG,Paint.Align.CENTER,face());
        text(c,Float.isNaN(kmh)?"—":String.format(java.util.Locale.US,"%03d",Math.round(kmh)),694,NUM_Y,NUM,FG,Paint.Align.CENTER,face());
        label(c,"RPM",107);label(c,"KMH",694);
        c.restore();
    }
    private void label(Canvas c,String s,float x){boolean own=FACE_ITALIC!=null;if(!own)p.setTextSkewX(-0.25f);text(c,s,x,LBL_Y,24,FG,Paint.Align.CENTER,italic());p.setTextSkewX(0);}   // no Inter: a slanted system bold
}
