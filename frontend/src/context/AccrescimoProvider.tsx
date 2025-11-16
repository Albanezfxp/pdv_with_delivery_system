import React, { createContext, useState, useContext, ReactNode } from 'react';

interface AccrescimoContextData {
  accrescimo: number;
  setAccrescimo: (val: number) => void;
}

const AccrescimoContext = createContext<AccrescimoContextData>({} as AccrescimoContextData);

export const AccrescimoProvider = ({ children }: { children: ReactNode }) => {
  const [accrescimo, setAccrescimo] = useState<number>(0.1);

  return (
    <AccrescimoContext.Provider value={{ accrescimo, setAccrescimo }}>
      {children}
    </AccrescimoContext.Provider>
  );
};

export const useAccrescimo = () => useContext(AccrescimoContext);