import {FC, useEffect, useState} from "react";
import {PlayerSettingsProps} from "../classes/Props.ts";
import SliderCheckbox from "./SliderCheckbox.tsx";
import "./PlayerSettings.css";
import {GameState} from "../classes/GameState.ts";
import {Player} from "../classes/Player.ts";

const PlayerSettings: FC<PlayerSettingsProps> = ({color, defaultIsAi, hasStart, setPlayer, onStart}) => {
    const [isAi, setIsAi] = useState(defaultIsAi);
    const [maxTime, setMaxTime] = useState(3000);
    const [maxMemory, setMaxMemory] = useState(16);

    useEffect(() => {
        let player: Player = {
            isAi: isAi,
            maxTime: maxTime,
            maxMemory: maxMemory
        };
        setPlayer(player);
    }, [isAi, maxTime, maxMemory]);

    return (
        <div id="settings" className="flex-container gap-2 m-2 p-2">
            <div className="grid-container">
                <p className="m-0">Player {color}</p>
                <SliderCheckbox isChecked={isAi} setIsChecked={setIsAi}/>
            </div>
            {isAi && <div className="grid-container">
                <label htmlFor="max-time-input">Max Time (ms)</label>
                <input id="max-time-input" type="number" min="0" max="60000" step="500"
                   placeholder="Time in ms" defaultValue={maxTime}
                   onChange={(event) => {
                       if (event.target.valueAsNumber > 60000) event.target.value = "60000";
                       else if (event.target.valueAsNumber < 0) event.target.value = "0";

                       setMaxTime(event.target.valueAsNumber);
                   }}/>
            </div>}
            {isAi && <div className="grid-container">
                <label htmlFor="max-memory-input">Table Size (MB)</label>
                <input id="max-memory-input" type="number" min="0" max="64" step="1"
                       placeholder="Table size in MB" defaultValue={maxMemory}
                       onChange={(event) => {
                           if (event.target.valueAsNumber > 64) event.target.value = "64";
                           else if (event.target.valueAsNumber < 0) event.target.value = "0";

                           setMaxMemory(event.target.valueAsNumber);
                       }}/>
            </div>}
            {isAi && <div className="grid-container">
                <label htmlFor="version-select">Version</label>
                <select id="version-select">
                    <option value={"v1"}>2D Array</option>
                    <option value={"v2"}>Bitboard</option>
                </select>
            </div>}
            {isAi && hasStart && <button className="btn btn-primary mt-2" onClick={onStart}>
                <i className="bi bi-play-circle"></i> Play
            </button>}
        </div>
    );
};

export default PlayerSettings;