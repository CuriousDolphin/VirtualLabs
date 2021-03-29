export interface StudentAssignment {
  id: number,
  title: string;
  status: string;
  votes: number;
  releaseDate: Date;
  expiryDate: Date;
  content: string;
}