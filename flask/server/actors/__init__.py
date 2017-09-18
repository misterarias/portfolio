class Actor(object):
    def __init__(self, name, company, description, icon):
        self.name = name
        self.company = company
        self.description = description
        self.image_name = icon
        self.role = None
        self.company_url = None
        self.social = []

    def setRole(self, role):
        self.role = role
        return self

    def setCompanyUrl(self, company_url):
        self.company_url = company_url
        return self

    def addSocial(self, social_fa_icon, social_url):
        self.social.append({
            'url': social_url,
            'icon': social_fa_icon
            })
        return self

juan = Actor(
        "Juan Arias",
        "Schibsted Products&Technology",
        "I've been working with computer systems for the past ten years, and been learning new things every day. I consider myself a very curious person, so getting expertise on Big Data systems was something I had been looking forward for a long time. Looking for the next challenge!",
        "juan-icon.jpg"
)
juan.setRole("Developer Advocate") \
    .setCompanyUrl("http://www.schibsted.com/") \
    .addSocial("fa-twitter", "https://twitter.com/ariasFreire") \
    .addSocial("fa-github", "https://github.com/misterarias/") \
    .addSocial("fa-linkedin", "https://www.linkedin.com/profile/view?id=96626628")

utad = Actor(
        "Big Data Expert",
        "U-Tad Madrid",
        "The U-tad's Certificate program in Big Data allows students to lead the design, management and exploitation of next generation infrastructures handling large volumes of data.",
        "utad-icon.png"
)
utad.setCompanyUrl("https://www.u-tad.com/en/studies/big-data-expert/") \
    .addSocial("fa-twitter", "https://twitter.com/U_tad") \
    .addSocial("fa-facebook", "https://es-es.facebook.com/utadcentrouniversitario") \
    .addSocial("fa-linkedin", "http://www.linkedin.com/company/u-tad")
