import "../styles/loading.css";

interface LoadingProps {
  text: string;
}

export default function Loading({ text }: LoadingProps) {
  return (
    <div className="loading-content">
      <div className="loading-spinner"></div>

      <div className="loading-text">
        <h3>{text}</h3>
        <p>Aguarde um momento...</p>
      </div>
    </div>
  );
}
