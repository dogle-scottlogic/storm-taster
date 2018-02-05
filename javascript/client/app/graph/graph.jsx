import React from 'react';
import * as d3 from 'd3';
import * as fc from 'd3fc'

import './graph.scss';

const width = 500;
const height = 500;
let graph;
let tooltip = null;

class Graph extends React.Component { 

    constructor(props) {
        super(props);
        this.setRef = this.setRef.bind(this);
    }

    render() {
        return (
            <div ref={this.setRef}>
                {graph && this.renderChart()}
            </div>
        );
    }

    renderChart() {
        const data = this.props.categories;
        var yExtent = fc.extentLinear()
            .include([0])
            .pad([0, 0.5])
            .accessors([function (d) { return d.value; }]);
        var valueformatter = d3.format("$.0f");

        // START
        var chart = fc.chartSvgCartesian(
            d3.scaleBand(),
            d3.scaleLinear())
            .chartLabel("Kick Starter - Total funding by category (USD)")
            .xDomain(data.map(function (d) { return d.category; }))
            .yDomain(yExtent(data))
            .yTicks(5)
            .xPadding(0.2)
            .yTickFormat(valueformatter)
            .yLabel("Million US Dollars")
            .yNice();
        // END

        var series = fc.autoBandwidth(fc.seriesSvgBar())
            .align("left")
            .crossValue((d) => d.category)
            .mainValue((d) => d.value)
            .decorate((selection) => {
                selection.on("mouseover", function (node, b, c) {
                    const bar = d3.select(this).node().getBoundingClientRect();
                    const left = bar.left + (bar.width / 2) - tooltip.node().getBoundingClientRect().width / 2;
                    tooltip.style("visibility", "visible");
                    tooltip.style("top", bar.y - tooltip.node().getBoundingClientRect().height);
                    tooltip.style("left", left);
                    tooltip.text("$" + node.value + "M");
                })
                selection.on("mouseout", function (node, b, c) {
                    tooltip.style("visibility", "hidden");
                })
            });

        chart.plotArea(series);
        if (data.length > 0) {
            d3.select(graph)
                .datum(data)
                .call(chart);
        }
    }

    setRef(ref) {
        graph = ref;
        tooltip = d3.select(graph)
            .append("div")
            .style("position", "absolute")
            .style("z-index", "10")
            .style("visibility", "hidden")
            .text("");
        this.renderChart();
    }
}

export default Graph;