import React from 'react';
import Graph from './graph';
import { getCategories } from '../api/categories';
import { subscribeToKSData } from '../api/categories';
import * as d3 from 'd3';

class GraphContainer extends React.Component {

    constructor(props) {
        super(props);
        this.valueformatter = d3.format("$.0f");
        this.state = {
            categories: [],
            backers: []
        }
    }

    componentWillMount() {
        // Subscribe to socket
        subscribeToKSData((err, ks_data) => {
            this.parseData(
                ks_data[0],
                this.convertFromENotation,
                (convertedData) => this.setState({ categories: convertedData }));
            this.parseData(
                ks_data[1],
                (value) => value != "" && parseInt(value),
                (convertedData) => this.setState({ backers: convertedData }));
        });
    }

    render() {
        return (
            <div className="charts">
                <Graph
                    className={"categories-chart"}    
                    ksData={this.state.categories}
                    valueformatter={this.valueformatter}
                    label={"Kick Starter - Total funding by category (USD)"}
                    yLabel={"Million US Dollars"}
                >
                </Graph>
                <Graph
                    className={"backers-chart"}        
                    ksData={this.state.backers}
                    label={"Kick Starter - Total backers by category"}
                    yLabel="No. of Backers"
                >
                </Graph>
            </div>    
        );
    }

    parseData(data, converter, setState) {
        const convertedData = [];
        for (let prop in data) {
            if (data.hasOwnProperty(prop)) {
                convertedData.push({
                    category: prop,
                    "value": converter(data[prop])
                });
            }
        }
        setState(convertedData);
    } 

    convertFromENotation(eNumber) {
        const parts = eNumber.split('E');
        let result = 0;
        parts.length < 2 ? result = parseFloat(eNumber) :
            result = parseFloat(Math.pow(10, parts[1]) * parts[0]);
        return parseFloat((result / 1000000).toFixed(2));
    }

}

export default GraphContainer;