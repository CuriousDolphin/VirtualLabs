import { Byte } from "@angular/compiler/src/util";

export interface User {
  iat: number;
  exp: number; // token expiration
  sub: string; // username
  roles: string[];
  userId: string;
  photo: string;
}
