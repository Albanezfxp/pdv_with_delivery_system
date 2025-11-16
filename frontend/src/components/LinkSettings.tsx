import { Link } from "react-router-dom";

interface LinkSettingsProps {
    path: string
    srcImg: string
    altImg: string
    title: string
}

export default function LinkSettings({path, srcImg, altImg, title}: LinkSettingsProps) {
    return <>
            <div id="link-settings-container">     
            <Link
              to={`${path}`}
              className={"link-container-item"}
            >
                <img src={srcImg} alt={`${altImg}Icon`} />
                    <h3>{title}</h3>
            </Link>
            </div>
    </>
}