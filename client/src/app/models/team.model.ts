import { Student } from "./student.model";

export interface Team {
  id: number;
  status: number;
  name: string;
  members: Student[];
  owner: Student;
  members_status: any;
  confirmation_token?: string;
  expiry_date?: string;
}
