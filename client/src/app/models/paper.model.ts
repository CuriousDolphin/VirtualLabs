import { Student } from './student.model';

export interface Paper {
  id: number,
  status: string,
  vote: number,
  lastUpdateTime: Date
  student: Student
}