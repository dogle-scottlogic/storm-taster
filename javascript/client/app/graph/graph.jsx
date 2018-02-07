import React from 'react';
import * as d3 from 'd3';
import * as fc from 'd3fc'

import './graph.scss';

const width = 250;
const height = 250;
let tooltip = null;

class Graph extends React.Component {

    constructor(props) {
        super(props);
        this.graph = null;
        this.setRef = this.setRef.bind(this);
    }

    componentWillMount() {
        this.setRef.bind(this);
    }

    render() {
        return (
            <div className={`graph ${this.props.className}`} ref={this.setRef}>
                {this.graph && this.renderChart()}
            </div>
        );
    }

    renderChart() {
        const data = this.props.ksData;
        var yExtent = fc.extentLinear()
            .include([0])
            .pad([0, 0.5])
            .accessors([function (d) { return d.value; }]);

        // START
        var chart = fc.chartSvgCartesian(
            d3.scaleBand(),
            d3.scaleLinear())
            .chartLabel(this.props.label)
            .xDomain(data.map(function (d) { return d.category; }))
            .yDomain(yExtent(data))
            .yTicks(5)
            .xPadding(0.2)
            .yTickFormat(this.props.valueformatter)
            .yLabel(this.props.yLabel)
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
                    tooltip.text(node.value);
                })
                selection.on("mouseout", function (node, b, c) {
                    tooltip.style("visibility", "hidden");
                })
            });

        chart.plotArea(series);
        if (data.length > 0) {
            d3.select(this.graph)
                .datum(data)
                .call(chart);
        }
    }

    setRef(ref) {
        this.graph = ref;
        tooltip = d3.select(this.graph)
            .append("div")
            .style("position", "absolute")
            .style("z-index", "10")
            .style("visibility", "hidden")
            .attr("class", "rollover")
            .text("");
        this.renderChart();
    }
}

export default Graph;