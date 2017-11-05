package com.appcook.user.graphics;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.androidplot.util.PixelUtils;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.StepMode;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Spinner fx;
    private List<String> formula;
    private XYPlot plot;
    private Button show;
    private PieChart mPieChart;
    private Button piechart;
    private TextView pieText;
    int positionSpinner = 3;

    public void initXML(){
        fx = (Spinner)findViewById(R.id.Sfx);
        plot = (XYPlot) findViewById(R.id.plot);
        show = (Button) findViewById(R.id.show);
        piechart = (Button) findViewById(R.id.showPie);
        pieText = (TextView) findViewById(R.id.TVPie);
        mPieChart = (PieChart) findViewById(R.id.piechart);
    }
    public void init(){
        formula = new ArrayList<>();
        formula.add("f(x) = 2*x + 14");
        formula.add("f(x) = x^2");
        formula.add("f(x) = sin(x)");
        formula.add("f(x) = cos(x)");
        formula.add("f(x) = e^x");
        formula.add("f(x) = ln(x)");
        formula.add("f(x) = x^3");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, formula);
        fx.setAdapter(adapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (("SM-T537V").startsWith(Build.MODEL.toString())) {
            setContentView(R.layout.activity_main);

            initXML();
            init();

            plot.setVisibility(View.GONE);
            mPieChart.setVisibility(View.GONE);
            piechart.setVisibility(View.GONE);
            pieText.setVisibility(View.GONE);

            show.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    plot.clear();
                    plot.redraw();
                    plot.setVisibility(View.VISIBLE);
                    piechart.setVisibility(View.VISIBLE);
                    pieText.setVisibility(View.VISIBLE);

                    plot.setDomainStep(StepMode.INCREMENT_BY_VAL, 1);
                    plot.setRangeStep(StepMode.INCREMENT_BY_VAL, 1);

                    plot.centerOnDomainOrigin(0);
                    plot.centerOnRangeOrigin(0);

                    LineAndPointFormatter series1Format0 = new LineAndPointFormatter(Color.RED, Color.BLACK, null, null);
                    LineAndPointFormatter series1Format1 = new LineAndPointFormatter(Color.BLACK, Color.GREEN, null, null);
                    LineAndPointFormatter series1Format2 = new LineAndPointFormatter(Color.YELLOW, Color.BLUE, null, null);
                    LineAndPointFormatter series1Format3 = new LineAndPointFormatter(Color.MAGENTA, Color.BLACK, null, null);
                    LineAndPointFormatter series1Format4 = new LineAndPointFormatter(Color.BLUE, Color.RED, null, null);

                    plot.getGraph().setLineLabelRenderer(XYGraphWidget.Edge.BOTTOM, new MyLineLabelRenderer());
                    plot.getGraph().setLineLabelRenderer(XYGraphWidget.Edge.LEFT, new MyLineLabelRenderer());

                    DashPathEffect dashFx = new DashPathEffect(new float[] {PixelUtils.dpToPix(3), PixelUtils.dpToPix(3)}, 0);

                    plot.getGraph().getDomainGridLinePaint().setPathEffect(dashFx);
                    plot.getGraph().getRangeGridLinePaint().setPathEffect(dashFx);

                    positionSpinner = fx.getSelectedItemPosition();
                    yPositive=0;
                    yNegative=0;
                    switch (positionSpinner){
                        case 0: plot.addSeries(generateSeries(-5, 5, 100, 0), series1Format0);
                            break;
                        case 1: plot.addSeries(generateSeries(-5, 5, 100, 1), series1Format1);
                            break;
                        case 2: plot.addSeries(generateSeries(-5, 5, 100, 2), series1Format2);
                            break;
                        case 3: plot.addSeries(generateSeries(-5, 5, 100, 3), series1Format3);
                            break;
                        case 4: plot.addSeries(generateSeries(-2.5, 2.5, 100, 4), series1Format4);
                            break;
                        case 5: plot.addSeries(generateSeries(0.1, 10.1, 100, 5), series1Format2);
                            break;
                        case 6: plot.addSeries(generateSeries(-2.5, 2.5, 100, 6), series1Format3);
                            break;
                    }
                    piechart.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            mPieChart.clearChart();
                            mPieChart.setVisibility(View.VISIBLE);
                            mPieChart.setFocusableInTouchMode(true);
                            mPieChart.addPieSlice(new PieModel("Positive", yPositive, Color.parseColor("#FE6DA8")));
                            mPieChart.addPieSlice(new PieModel("Negative", yNegative, Color.parseColor("#56B7F1")));
                            mPieChart.setInnerPadding(50);

                            String yPos = Integer.toString(yPositive-1);
                            String yNeg = Integer.toString(yNegative);
                            mPieChart.setInnerValueString("Negatives: "+yNeg+"% "+'\n'+"   Positives: "+yPos+"%");
                            mPieChart.setInnerValueSize(25);
                            mPieChart.startAnimation();
                        }
                    });
                }
            });
        } else {
            setContentView(R.layout.activity_main_no_play);
        }
    }

    double yValue= 0.0;
    int yPositive =0;
    int yNegative =0;
    protected XYSeries generateSeries(double minX, double maxX, double resolution, int positionSpinner) {
        final double range = maxX - minX;
        final double step = range / resolution;
        List<Number> xVals = new ArrayList<>();
        List<Number> yVals = new ArrayList<>();

        double x = minX;
        while (x <= maxX) {
            xVals.add(x);
            yVals.add(fx(x, positionSpinner));
            x +=step;
            yValue = fx(x, positionSpinner);
            if(yValue>0){
                yPositive++;
            }else{
                yNegative++;
            }
        }

        String fxLabel = "";
        switch (positionSpinner) {
            case 0:  fxLabel = "f(x) = 2*x + 14";
                break;
            case 1:  fxLabel = "f(x) = x^2";
                break;
            case 2:  fxLabel = "f(x) = sin(x)";
                break;
            case 3:  fxLabel = "f(x) = cos(x)";
                break;
            case 4:  fxLabel = "f(x) = e^x";
                break;
            case 5:  fxLabel = "f(x) = ln(x)";
                break;
            case 6: fxLabel = "f(x) = x^3";
        }

        return new SimpleXYSeries(xVals, yVals, fxLabel);


    }

    protected double fx(double x, int positionSpinner) {
        double fx = 0;
        switch (positionSpinner) {
            case 0:  fx = 2*x+3;
                break;
            case 1:  fx = (x*x) - 14;
                break;
            case 2:  fx = Math.sin(x);
                break;
            case 3:  fx = Math.cos(x);
                break;
            case 4:  fx = Math.pow(Math.E,x);
                break;
            case 5:  fx = Math.log(x);
                break;
            case 6:  fx = Math.pow(x,3);
        }
        return fx;
    }

    class MyLineLabelRenderer extends XYGraphWidget.LineLabelRenderer {
        @Override
        protected void drawLabel(Canvas canvas, String text, Paint paint, float x, float y, boolean isOrigin) {
            if(isOrigin) {
                final Paint originPaint = new Paint(paint);
                originPaint.setColor(Color.RED);
                super.drawLabel(canvas, text, originPaint, x, y , isOrigin);
            } else {
                super.drawLabel(canvas, text, paint, x, y , isOrigin);
            }
        }
    }
}
