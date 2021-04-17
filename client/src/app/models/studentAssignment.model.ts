export interface StudentAssignment {
  id: number,
  title: string;
  status: string;
  vote: number;
  releaseDate: Date;
  expiryDate: Date;
  content: string;
}