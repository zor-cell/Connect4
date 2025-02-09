import './SliderCheckbox.css'
import {FC} from "react";
import {SliderCheckBoxProps} from "../classes/Props.ts";

const SliderCheckbox: FC<SliderCheckBoxProps> = ({isChecked, setIsChecked}) => {
    return (
        <div className="button">
            <input type="checkbox"
                   className="checkbox"
                   defaultChecked={isChecked}
                   onChange={(event) => {
                       setIsChecked(event.target.checked);
                   }}/>
            <div className="knobs"></div>
        </div>
    )
}

export default SliderCheckbox;