import "../styles/MainContent.css"

interface MainContentProps {
  children: React.ReactNode;
}

export default function MainContent({ children }: MainContentProps) {
  return <main className="main-content">{children}</main>;
}