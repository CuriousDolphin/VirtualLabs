import { PaperSnapshot } from "./papersnapshot.model";

export interface SolutionFormData {
  toReview: boolean,
  vote: number,
  imgSource: ArrayBuffer,
  papersnapshot: PaperSnapshot
}