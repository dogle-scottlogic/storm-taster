import React from 'react';
import { render } from 'react-dom';
import GraphContainer from './graph/graphContainer';

class App extends React.Component {
    render() {
        return (
            <div>
                <GraphContainer />
            </div>        
        )
    }
}

render(<App />, document.getElementById('app'));