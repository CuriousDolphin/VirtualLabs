import { Student } from "./student.model";
import { VmInstance } from "./vm-instance.model";

export interface Team {
  id: number;
  status: number;
  name: string;
  members: Student[];
  owner: Student;
  members_status: any;
  confirmation_token?: string;
  expiry_date?: string;
  vmInstances: VmInstance[];
}
