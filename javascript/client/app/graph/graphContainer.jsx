import React from 'react';
import Graph from './graph';
import { getCategories } from '../api/categories';
import { subscribeToCategories } from '../api/categories';

class GraphContainer extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            categories: []
        }
    }

    componentWillMount() {
        const categoriesFromApi = [];
        // getCategories().then((result) => {
        //     for (let prop in result) {
        //         if (result.hasOwnProperty(prop)) {
        //             categoriesFromApi.push({
        //                 "category": prop,
        //                 "value": this.convertFromENotation(result[prop], prop)
        //             });
        //         }
        //     }
        //     this.setState({ categories: categoriesFromApi });
        // }).catch((err) => console.log(err));

        // Subscribe to socket
        subscribeToCategories((err, cats) => {
            for (let category in cats) {
                if (cats.hasOwnProperty(category)) {
                    categoriesFromApi.push({
                        category,
                        "value": this.convertFromENotation(cats[category], category)
                    });
                }
            }
            this.setState({ categories: categoriesFromApi });
        });
    }

    render() {
        return (
            <Graph
                categories={this.state.categories}
            />
        );
    }

    convertFromENotation(eNumber, prop) {
        const parts = eNumber.split('E');
        let result = 0;
        parts.length < 2 ? result = parseFloat(eNumber) :
            result = parseFloat(Math.pow(10, parts[1]) * parts[0]);
        return parseFloat((result / 1000000).toFixed(2));
    }

}

export default GraphContainer;