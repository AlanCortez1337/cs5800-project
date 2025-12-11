import {
  IconPackage,
  IconBowlSpoonFilled,
  IconUsersGroup,
  IconChartBarPopular
} from "@tabler/icons-react"

export const navLinks = {
  user: {
    name: "shadcn",
    email: "m@example.com",
  },
  navMain: [
    {
      title: "Inventory",
      url: "/dashboard",
      icon: IconPackage,
    },
    {
      title: "Recipes",
      url: "/dashboard/recipes",
      icon: IconBowlSpoonFilled,
    },
    {
      title: "Staff",
      url: "/dashboard/staff",
      icon: IconUsersGroup,
    },
    {
      title: "Reports",
      url: "/dashboard/reports",
      icon: IconChartBarPopular,
    },
  ],
}