package conquest.view;

import java.awt.Color;

import conquest.game.Team;

public class TeamView {

    public static final int[] PLAYER_1_COLOR_RGB = new int[] { 160, 160, 255 };
    public static final int[] PLAYER_2_COLOR_RGB = new int[] { 255, 160, 160 };    
    public static final int[] NEUTRAL_COLOR_RGB = new int[] { 240, 240, 240 };
    
    public static final int[] PLAYER_1_HIGHLIGHT_COLOR_RGB = new int[] { 180, 180, 255 };
    public static final int[] PLAYER_2_HIGHLIGHT_COLOR_RGB = new int[] { 255, 180, 180 };
    public static final int[] NEUTRAL_HIGHLIGHT_COLOR_RGB = new int[] { 245, 245, 245 };
    
    public static final float[] PLAYER_1_COLOR_HSB = Color.RGBtoHSB(PLAYER_1_COLOR_RGB[0], PLAYER_1_COLOR_RGB[1], PLAYER_1_COLOR_RGB[2], null);
    public static final float[] PLAYER_2_COLOR_HSB = Color.RGBtoHSB(PLAYER_2_COLOR_RGB[0], PLAYER_2_COLOR_RGB[1], PLAYER_2_COLOR_RGB[2], null);
    public static final float[] NEUTRAL_COLOR_HSB = Color.RGBtoHSB(NEUTRAL_COLOR_RGB[0], NEUTRAL_COLOR_RGB[1], NEUTRAL_COLOR_RGB[2], null);
    public static final float[] PLAYER_1_HIGHLIGHT_COLOR_HSB = Color.RGBtoHSB(PLAYER_1_HIGHLIGHT_COLOR_RGB[0], PLAYER_1_HIGHLIGHT_COLOR_RGB[1], PLAYER_1_HIGHLIGHT_COLOR_RGB[2], null);
    public static final float[] PLAYER_2_HIGHLIGHT_COLOR_HSB = Color.RGBtoHSB(PLAYER_2_HIGHLIGHT_COLOR_RGB[0], PLAYER_2_HIGHLIGHT_COLOR_RGB[1], PLAYER_2_HIGHLIGHT_COLOR_RGB[2], null);
    public static final float[] NEUTRAL_HIGHLIGHT_COLOR_HSB = Color.RGBtoHSB(NEUTRAL_HIGHLIGHT_COLOR_RGB[0], NEUTRAL_HIGHLIGHT_COLOR_RGB[1], NEUTRAL_HIGHLIGHT_COLOR_RGB[2], null);
    
    public static final Color PLAYER_1_COLOR = Color.getHSBColor(PLAYER_1_COLOR_HSB[0], PLAYER_1_COLOR_HSB[1], PLAYER_1_COLOR_HSB[2]);
    public static final Color PLAYER_1_HIGHLIGHT_COLOR = Color.getHSBColor(PLAYER_1_HIGHLIGHT_COLOR_HSB[0], PLAYER_1_HIGHLIGHT_COLOR_HSB[1], PLAYER_1_HIGHLIGHT_COLOR_HSB[2]);
    
    public static final Color PLAYER_2_COLOR = Color.getHSBColor(PLAYER_2_COLOR_HSB[0], PLAYER_2_COLOR_HSB[1], PLAYER_2_COLOR_HSB[2]);
    public static final Color PLAYER_2_HIGHLIGHT_COLOR = Color.getHSBColor(PLAYER_2_HIGHLIGHT_COLOR_HSB[0], PLAYER_2_HIGHLIGHT_COLOR_HSB[1], PLAYER_2_HIGHLIGHT_COLOR_HSB[2]);
    
    public static final Color NEUTRAL_COLOR = Color.getHSBColor(NEUTRAL_COLOR_HSB[0], NEUTRAL_COLOR_HSB[1], NEUTRAL_COLOR_HSB[2]);
    public static final Color NEUTRAL_HIGHLIGHT_COLOR = Color.getHSBColor(NEUTRAL_HIGHLIGHT_COLOR_HSB[0], NEUTRAL_HIGHLIGHT_COLOR_HSB[1], NEUTRAL_HIGHLIGHT_COLOR_HSB[2]);
    
    public static Color getColor(Team team) {
        switch(team) {
        case NEUTRAL: return NEUTRAL_COLOR;
        case PLAYER_1: return PLAYER_1_COLOR;
        case PLAYER_2: return PLAYER_2_COLOR;
        }
        return Color.BLACK;
    }
    
    public static Color getHighlightColor(Team team) {
        switch(team) {
        case NEUTRAL: return NEUTRAL_HIGHLIGHT_COLOR;
        case PLAYER_1: return PLAYER_1_HIGHLIGHT_COLOR;
        case PLAYER_2: return PLAYER_2_HIGHLIGHT_COLOR;
        }
        return Color.BLACK;
    }
    
    
}
