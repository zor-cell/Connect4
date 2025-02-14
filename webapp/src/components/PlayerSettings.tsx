import {FC, useEffect, useState} from "react";
import {PlayerSettingsProps} from "../classes/Props.ts";
import SliderCheckbox from "./SliderCheckbox.tsx";
import "./PlayerSettings.css";
import {GameState} from "../classes/GameState.ts";
import {Player} from "../classes/Player.ts";
import {Version} from "../classes/Version.ts";

const PlayerSettings: FC<PlayerSettingsProps> = ({color, defaultIsAi, hasStart, setPlayer, onStart}) => {
    const [isAi, setIsAi] = useState(defaultIsAi);
    const [maxTime, setMaxTime] = useState(3000);
    const [maxMemory, setMaxMemory] = useState(64);
    const [version, setVersion] = useState(Version.V2_0);

    useEffect(() => {
        let player: Player = {
            isAi: isAi,
            maxTime: maxTime,
            maxMemory: maxMemory,
            version: version
        };
        setPlayer(player);
    }, [isAi, maxTime, maxMemory, version]);

    function fromVersionString(versionString: string): Version {
        if(versionString == "1.0") {
            return Version.V1_0;
        } else if(versionString == "1.1") {
            return Version.V1_1;
        } else if(versionString == "2.0") {
            return Version.V2_0;
        } else if(versionString == "2.1") {
            return Version.V2_1;
        }

        return Version.V1_0;
    }

    function toVersionString(version: Version): string {
        if(version == Version.V1_0) {
            return "1.0";
        } else if(version == Version.V1_1) {
            return "1.1";
        } else if(version == Version.V2_0) {
            return "2.0";
        } else if(version == Version.V2_1) {
            return "2.1";
        }

        return "1.0";
    }

    return (
        <div id="settings" className="flex-container gap-2 m-2 p-2">
            <div className="grid-container">
                <p className="m-0">Player {color}</p>
                <SliderCheckbox isChecked={isAi} setIsChecked={setIsAi}/>
            </div>
            {isAi && <div className="grid-container">
                <label htmlFor="max-time-input">Max Time (ms)</label>
                <input id="max-time-input" className="custom-form-input" type="number" min="0" max="60000" step="500"
                   placeholder="Time in ms" defaultValue={maxTime}
                   onChange={(event) => {
                       if (event.target.valueAsNumber > 60000) event.target.value = "60000";
                       else if (event.target.valueAsNumber < 0) event.target.value = "0";

                       setMaxTime(event.target.valueAsNumber);
                   }}/>
            </div>}
            {isAi && <div className="grid-container">
                <label htmlFor="max-memory-input">Table Size (MB)</label>
                <input id="max-memory-input" className="custom-form-input" type="number" min="0" max="256" step="1"
                       placeholder="Table size in MB" defaultValue={maxMemory}
                       onChange={(event) => {
                           if (event.target.valueAsNumber > 256) event.target.value = "256";
                           else if (event.target.valueAsNumber < 0) event.target.value = "0";

                           setMaxMemory(event.target.valueAsNumber);
                       }}/>
            </div>}
            {isAi && <div className="grid-container">
                <label htmlFor="version-select">Version</label>
                <select id="version-select" className="custom-form-input" defaultValue={toVersionString(version)}
                        onChange={(event) => {
                            let curVersion = fromVersionString(event.target.value);
                            setVersion(curVersion);
                        }}>
                    <option value={"1.0"}>v1.0 (2D)</option>
                    <option value={"1.1"}>v1.1 (2D+T)</option>
                    <option value={"2.0"}>v2.0 (BB)</option>
                    <option value={"2.1"}>v2.1 (BB+T)</option>
                </select>
            </div>}
            {isAi && hasStart && <button className="btn btn-primary mt-2" onClick={onStart}>
                <i className="bi bi-play-circle"></i> Play
            </button>}
        </div>
    );
};

export default PlayerSettings;