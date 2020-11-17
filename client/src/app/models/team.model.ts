import { Student } from "./student.model";

export interface Team {
  id: number;
  status: number;
  name: string;
  members: Student[];
}
