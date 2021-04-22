package cf.khanhsb.icare_v2;

import android.graphics.drawable.Drawable;

import java.util.ArrayList;

public class MealPlanData {
    private ArrayList<String> titles;
    private ArrayList<String> kcal;
    private ArrayList<String> detailOfMeal;
    private ArrayList<Integer> icons;
    private ArrayList<Drawable> backgrounds;

    public void setIcons(ArrayList<Integer> icons) {
        this.icons = icons;
    }

    public void setBackgrounds(ArrayList<Drawable> backgrounds) {
        this.backgrounds = backgrounds;
    }

    public void setDetailOfMeal(ArrayList<String> detailOfMeal) {
        this.detailOfMeal = detailOfMeal;
    }

    public void setKcal(ArrayList<String> kcal) {
        this.kcal = kcal;
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

    public ArrayList<String> getKcal() {
        return kcal;
    }

    public ArrayList<String> getTitles() {
        return titles;
    }
}
