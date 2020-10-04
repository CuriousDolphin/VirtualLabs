export interface Course {
    name:    string;
    acronym: string;
    min:     number;
    max:     number;
    enabled: boolean;
    links?:   Link[];
}

export interface Link {
    rel:  string;
    href: string;
}