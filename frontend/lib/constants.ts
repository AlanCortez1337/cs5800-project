import {
  IconPackage,
  IconBowlSpoonFilled,
  IconUsersGroup
} from "@tabler/icons-react"

export const navLinks = {
  user: {
    name: "shadcn",
    email: "m@example.com",
  },
  navMain: [
    {
      title: "Inventory",
      url: "/inventory",
      icon: IconPackage,
    },
    {
      title: "Recipes",
      url: "/recipes",
      icon: IconBowlSpoonFilled,
    },
    {
      title: "Staff",
      url: "/staff",
      icon: IconUsersGroup,
    },
    
  ],
}