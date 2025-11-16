interface GeralTitleProps {
title: string

}

export default function GeralTitle({ title}: GeralTitleProps){

    return <>
              <div className={`geral-title`}>
            <h1>{title}</h1>
            <hr />
          </div></>
}