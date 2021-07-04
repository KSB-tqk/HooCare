package cf.khanhsb.icare_v2.Model;

import android.graphics.drawable.Drawable;

import java.util.ArrayList;

public class MealPlanData {
    private ArrayList<String> titles;
    private ArrayList<String> detailOfMeal;
    private ArrayList<Integer> icons;
    private ArrayList<Drawable> backgrounds;

    public MealPlanData(ArrayList<String> titles,ArrayList<String> detailOfMeal,
                         ArrayList<Integer> icons,
                        ArrayList<Drawable> backgrounds){
        this.detailOfMeal = detailOfMeal;
        this.titles = titles;
        this.icons = icons;
        this.backgrounds = backgrounds;
    }
    public void setIcons(ArrayList<Integer> icons) {
        this.icons = icons;
    }

    public void setBackgrounds(ArrayList<Drawable> backgrounds) {
        this.backgrounds = backgrounds;
    }

    public void setDetailOfMeal(ArrayList<String> detailOfMeal) {
        this.detailOfMeal = detailOfMeal;
    }

    public void setTitles(ArrayList<String> titles) {
        this.titles = titles;
    }

    public ArrayList<Drawable> getBackgrounds() {
        return backgrounds;
    }

    public ArrayList<Integer> getIcons() {
        return icons;
    }

    public ArrayList<String> getDetailOfMeal() {
        return detailOfMeal;
    }

    public ArrayList<String> getTitles() {
        return titles;
    }
}
