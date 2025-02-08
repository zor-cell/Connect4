import './SliderCheckbox.css'

const SliderCheckbox = () => {
    return (
        <div className="button">
            <input type="checkbox" className="checkbox" defaultChecked={true}/>
            <div className="knobs"></div>
            <div className="layer"></div>
        </div>
    )
}

export default SliderCheckbox;