package com.ishmeetgrewal.zerodegrees;

import android.test.ActivityInstrumentationTestCase2;
import android.view.Display;

import com.robotium.solo.Solo;

import junit.framework.Assert;


public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity>{

    private Solo solo;

    public MainActivityTest(){

        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception{
        super.setUp();
        solo = new Solo(getInstrumentation(), getActivity());
    }

    @Override
    protected void tearDown() throws Exception{
        solo.finishOpenedActivities();
        super.tearDown();
    }

    public void testWeatherShouldRender() throws Exception{
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);

        Assert.assertTrue(solo.searchText("Columbus"));
    }

    public void testSwipeToRightShouldOpenDrawer() throws Exception{
        Display display = solo.getCurrentActivity().getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();
        float xStart = 0 ;
        float xEnd = width / 2;
        solo.drag(xStart, xEnd, height / 2, height / 2, 1);

        Assert.assertTrue(solo.searchText("Clothes"));
        Assert.assertTrue(solo.searchText("Locations"));
        Assert.assertTrue(solo.searchText("Home"));
    }

    public void testLocationsHasHome() throws Exception{
        Display display = solo.getCurrentActivity().getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();
        float xStart = 0 ;
        float xEnd = width / 2;
        solo.drag(xStart, xEnd, height / 2, height / 2, 1);

        solo.clickOnText("Locations");
        Assert.assertTrue(solo.searchText("Columbus"));

    }

    public void testClothesFragment() throws Exception{
        Display display = solo.getCurrentActivity().getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();
        float xStart = 0 ;
        float xEnd = width / 2;
        solo.drag(xStart, xEnd, height / 2, height / 2, 1);

        solo.clickOnText("Clothes");
        Assert.assertTrue(solo.getCurrentActivity().getResources().getDrawable(R.drawable.t_shirt).isVisible());

    }



}

